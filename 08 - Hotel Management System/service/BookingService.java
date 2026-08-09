package service;

import domain.*;
import repository.*;
import java.util.*;

public class BookingService {
    private BookingRepository bookingRepository;
    private HotelRepository hotelRepository;
    private RoomTypeRepository roomTypeRepository;
    private UserRepository userRepository;
    private InventoryService inventoryService;
    private PricingService pricingService;
    private PolicyService policyService;
    private CancellationPolicyRepository cancellationPolicyRepository;
    private TransactionService transactionService;

    private static final long HOLD_TTL_MS = 15 * 60 * 1000; // 15 minutes

    public BookingService(BookingRepository bookingRepository, HotelRepository hotelRepository,
            RoomTypeRepository roomTypeRepository, UserRepository userRepository,
            InventoryService inventoryService, PricingService pricingService,
            PolicyService policyService, CancellationPolicyRepository cancellationPolicyRepository,
            TransactionService transactionService) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.userRepository = userRepository;
        this.inventoryService = inventoryService;
        this.pricingService = pricingService;
        this.policyService = policyService;
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.transactionService = transactionService;
    }

    public Booking createBooking(String userId, String hotelId, String roomTypeId, DateRange range,
            long expectedTotalPrice) {
        // Validate user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // Validate roomType
        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(roomTypeId);
        if (roomTypeOpt.isEmpty()) {
            throw new IllegalArgumentException("RoomType not found: " + roomTypeId);
        }

        // Validate date range
        if (range.getCheckInDateUtc() >= range.getCheckOutDateUtc()) {
            throw new IllegalArgumentException("Invalid date range: check-in must be before check-out");
        }

        // Precheck availability
        if (!inventoryService.checkAvailability(hotelId, roomTypeId, range, 1)) {
            throw new IllegalStateException("No availability for the requested dates");
        }

        // Fetch current prices from database
        List<NightlyPrice> nightlyPrices = pricingService.rateStay(hotelId, roomTypeId, range);
        if (nightlyPrices.isEmpty()) {
            throw new IllegalStateException("Could not fetch prices");
        }

        // Calculate total
        long calculatedTotalPrice = pricingService.computeTotal(nightlyPrices);

        // Validate price if expected provided
        if (expectedTotalPrice > 0 && Math.abs(calculatedTotalPrice - expectedTotalPrice) > 100) { // tolerance of 1
                                                                                                   // unit (100 minor
                                                                                                   // units)
            throw new IllegalStateException(
                    "Price mismatch: expected " + expectedTotalPrice + ", got " + calculatedTotalPrice);
        }

        // Create booking with CREATED status (no inventory reduction, no hold expiry)
        long now = System.currentTimeMillis();
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(
                bookingId, userId, hotelId, roomTypeId,
                range.getCheckInDateUtc(), range.getCheckOutDateUtc(),
                nightlyPrices, calculatedTotalPrice,
                BookingStatus.CREATED, TransactionStatus.PENDING,
                null, 0, 0, // allocatedRoomId, checkInTimeUtc, checkOutTimeUtc
                0, now // holdExpiresAt=0 (no hold yet), createdAt
        );

        booking = bookingRepository.save(booking);
        // NO transaction initiation here - user will call initiateTransaction
        // separately

        return booking;
    }

    public void cancelBooking(String bookingId, String userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        Booking booking = bookingOpt.get();

        // Validate ownership
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User does not own this booking");
        }

        // Validate booking can be cancelled using state handler
        if (!BookingStateHandler.canCancel(booking)) {
            throw new IllegalStateException("Cannot cancel booking in current state: " + booking.getBookingStatus());
        }

        // Get cancellation policy
        Optional<Hotel> hotelOpt = hotelRepository.findById(booking.getHotelId());
        if (hotelOpt.isEmpty()) {
            throw new IllegalStateException("Hotel not found");
        }
        Hotel hotel = hotelOpt.get();

        Optional<CancellationPolicy> policyOpt = cancellationPolicyRepository.findById(hotel.getCancellationPolicyId());
        if (policyOpt.isEmpty()) {
            throw new IllegalStateException("Cancellation policy not found");
        }

        // Evaluate cancellation
        RefundDecision refundDecision = policyService.evaluateCancellation(booking, policyOpt.get(),
                System.currentTimeMillis());

        // Update booking status using state handler
        BookingStateHandler.transition(booking, BookingStatus.CANCELLED);
        if (refundDecision.getRefundAmountMinor() > 0) {
            booking.setPaymentStatus(TransactionStatus.REFUNDED);
            // Issue refund
            transactionService.issueRefund(bookingId, refundDecision.getRefundAmountMinor());
        }

        bookingRepository.save(booking);
        // Inventory automatically restored since CANCELLED bookings don't count in
        // availability
        // Call the PG to initiate the refund /.....
    }

    public Booking checkIn(String bookingId, String roomId, long checkInTimeUtc) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        Booking booking = bookingOpt.get();

        // Validate booking can be checked in using state handler
        BookingStateHandler.requireStatus(booking, BookingStatus.CONFIRMED);

        // Assign room
        booking.setAllocatedRoomId(roomId);
        booking.setCheckInTimeUtc(checkInTimeUtc);
        BookingStateHandler.transition(booking, BookingStatus.CHECKED_IN);

        return bookingRepository.save(booking);
    }

    public Booking checkOut(String bookingId, long checkOutTimeUtc) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        Booking booking = bookingOpt.get();

        // Validate booking can be checked out using state handler
        BookingStateHandler.requireStatus(booking, BookingStatus.CHECKED_IN);

        booking.setCheckOutTimeUtc(checkOutTimeUtc);
        BookingStateHandler.transition(booking, BookingStatus.CHECKED_OUT);

        // If early check-out (before checkOutDateUtc), inventory is automatically
        // restored
        // since CHECKED_OUT bookings don't count in availability

        return bookingRepository.save(booking);
    }

    // TODO: Background Scheduler - Process expired holds
    // This method should be called periodically by a background scheduler (e.g.,
    // every 1-5 minutes)
    // public void processExpiredHolds() {
    // long nowUtc = System.currentTimeMillis();
    // List<Booking> expiredBookings = findExpiredHeldBookings(nowUtc);
    // for (Booking booking : expiredBookings) {
    // booking.setBookingStatus(BookingStatus.CANCELLED);
    // booking.setPaymentStatus(TransactionStatus.FAILED);
    // bookingRepository.save(booking);
    // // Inventory automatically restored since CANCELLED bookings don't count
    // }
    // }
    //
    // private List<Booking> findExpiredHeldBookings(long nowUtc) {
    // // TODO: Add method to BookingRepository to find HELD bookings where
    // holdExpiresAt < nowUtc
    // // return bookingRepository.findExpiredHeldBookings(nowUtc);
    // return Collections.emptyList();
    // }
}

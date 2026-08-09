package main;

import controller.*;
import domain.*;
import repository.*;
import repository.impl.*;
import service.*;
import java.util.List;
import java.util.UUID;

public class HotelManagementSimulation {
    public static void main(String[] args) {
        System.out.println("=== HOTEL MANAGEMENT SYSTEM SIMULATION ===\n");

        // Initialize repositories
        HotelRepository hotelRepository = new HotelRepositoryImpl();
        RoomTypeRepository roomTypeRepository = new RoomTypeRepositoryImpl();
        RoomRepository roomRepository = new RoomRepositoryImpl();
        BookingRepository bookingRepository = new BookingRepositoryImpl();
        TransactionRepository transactionRepository = new TransactionRepositoryImpl();
        SeasonalPriceRepository seasonalPriceRepository = new SeasonalPriceRepositoryImpl();
        CancellationPolicyRepository cancellationPolicyRepository = new CancellationPolicyRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();

        // Initialize services
        PricingService pricingService = new PricingService(roomTypeRepository, seasonalPriceRepository);
        InventoryService inventoryService = new InventoryService(bookingRepository, hotelRepository,
                roomTypeRepository);
        PolicyService policyService = new PolicyService();
        TransactionService transactionService = new TransactionService(transactionRepository, bookingRepository,
                inventoryService);
        BookingService bookingService = new BookingService(
                bookingRepository, hotelRepository, roomTypeRepository, userRepository,
                inventoryService, pricingService, policyService, cancellationPolicyRepository, transactionService);
        SearchService searchService = new SearchService(
                hotelRepository, roomTypeRepository, roomRepository, pricingService, bookingRepository);
        UserService userService = new UserService(bookingRepository);

        // Initialize controllers
        SearchController searchController = new SearchController(searchService);
        BookingController bookingController = new BookingController(bookingService);
        TransactionController transactionController = new TransactionController(transactionService);
        AdminController adminController = new AdminController(
                hotelRepository, roomTypeRepository, roomRepository,
                seasonalPriceRepository, cancellationPolicyRepository, bookingService);
        DashboardController dashboardController = new DashboardController(userService);

        // Simulation
        System.out.println("1. Creating cancellation policy...");
        CancellationPolicy flexPolicy = new CancellationPolicy(
                UUID.randomUUID().toString(), "FLEX", 100, 24, System.currentTimeMillis());
        flexPolicy = adminController.createOrUpdatePolicy(flexPolicy);
        System.out.println("Created policy: " + flexPolicy);

        System.out.println("\n2. Creating hotel...");
        Hotel hotel = new Hotel(
                UUID.randomUUID().toString(), "Grand Hotel", "123 Main St",
                "New York", "USA", 40.7128, -74.0060, 4.5, true,
                10, flexPolicy.getId(), System.currentTimeMillis());
        hotel = adminController.createOrUpdateHotel(hotel);
        System.out.println("Created hotel: " + hotel);

        System.out.println("\n3. Creating room type...");
        RoomType deluxeKing = new RoomType(
                UUID.randomUUID().toString(), hotel.getId(), "Deluxe King",
                2, "KING", 10000, // $100.00 in minor units
                List.of("WiFi", "TV", "Mini Bar"), 10, true, System.currentTimeMillis());
        deluxeKing = adminController.createOrUpdateRoomType(deluxeKing);
        System.out.println("Created room type: " + deluxeKing);

        System.out.println("\n4. Creating user...");
        User customer = new User(
                UUID.randomUUID().toString(), "John Doe", "john@example.com",
                UserRole.CUSTOMER, System.currentTimeMillis());
        customer = userRepository.save(customer);
        System.out.println("Created user: " + customer);

        System.out.println("\n5. Setting seasonal price...");
        long tomorrow = System.currentTimeMillis() + 86400000;
        SeasonalPrice seasonalPrice = adminController.setSeasonalPrice(
                hotel.getId(), deluxeKing.getId(), tomorrow, 15000 // $150.00
        );
        System.out.println("Set seasonal price: " + seasonalPrice);

        System.out.println("\n6. Checking availability...");
        DateRange range = new DateRange(tomorrow, tomorrow + 2 * 86400000); // 2 nights
        List<RoomTypeAvailability> availableRoomTypes = searchController.getAvailability(
                hotel.getId(), range);
        System.out.println("Found " + availableRoomTypes.size() + " available room type(s):");
        for (RoomTypeAvailability rta : availableRoomTypes) {
            System.out.println("  - " + rta.getRoomTypeName() + " (Capacity: " + rta.getCapacity() +
                    ", Bed: " + rta.getBedType() + ", Price: " + rta.getTotalPrice() + ")");
        }

        System.out.println("\n7. Creating booking...");
        // Use first available room type for booking
        Booking booking = null;
        if (availableRoomTypes.isEmpty()) {
            System.out.println("No available room types for booking");
        } else {
            RoomTypeAvailability selectedRoomType = availableRoomTypes.get(0);
            booking = bookingController.createBooking(
                    customer.getId(), hotel.getId(), selectedRoomType.getRoomTypeId(), range,
                    selectedRoomType.getTotalPrice());
            System.out.println("Created booking: " + booking);
        }

        if (booking != null) {
            System.out.println("\n8. Initiating transaction...");
            Transaction transaction = transactionController.initiateTransaction(booking.getId());
            System.out.println("Transaction initiated: " + transaction);

            System.out.println("\n9. Simulating payment success callback...");
            transactionController.handleTransactionCallback(transaction.getProviderRef(), TransactionStatus.COMPLETED);
            System.out.println("Payment completed, booking confirmed");

            System.out.println("\n10. Admin checking in guest...");
            Room room = new Room(
                    UUID.randomUUID().toString(), hotel.getId(), booking.getRoomTypeId(),
                    "101", true, System.currentTimeMillis());
            room = roomRepository.save(room);
            Booking checkedInBooking = adminController.checkIn(booking.getId(), room.getId(),
                    System.currentTimeMillis());
            System.out.println("Guest checked in: " + checkedInBooking);

            System.out.println("\n11. Admin checking out guest (early check-out)...");
            Booking checkedOutBooking = adminController.checkOut(booking.getId(),
                    System.currentTimeMillis() + 86400000);
            System.out.println("Guest checked out: " + checkedOutBooking);
        }

        System.out.println("\n12. Viewing user dashboard...");
        List<Booking> userBookings = dashboardController.listUserBookings(customer.getId());
        System.out.println("User bookings: " + userBookings.size() + " booking(s)");

        System.out.println("\n=== SIMULATION COMPLETED ===");
    }
}
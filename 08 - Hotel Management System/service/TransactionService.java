package service;

import domain.*;
import repository.BookingRepository;
import repository.TransactionRepository;
import java.util.Optional;
import java.util.UUID;

public class TransactionService {
    private TransactionRepository transactionRepository;
    private BookingRepository bookingRepository;
    private InventoryService inventoryService;

    public TransactionService(TransactionRepository transactionRepository, BookingRepository bookingRepository,
            InventoryService inventoryService) {
        this.transactionRepository = transactionRepository;
        this.bookingRepository = bookingRepository;
        this.inventoryService = inventoryService;
    }

    public Transaction initiateTransaction(String bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        Booking booking = bookingOpt.get();

        // Verify booking can initiate transaction using state handler
        BookingStateHandler.requireStatus(booking, BookingStatus.CREATED);

        // Re-check availability before holding inventory
        DateRange range = new DateRange(booking.getCheckInDateUtc(), booking.getCheckOutDateUtc());
        if (!inventoryService.checkAvailability(booking.getHotelId(), booking.getRoomTypeId(), range, 1)) {
            throw new IllegalStateException("No availability for the requested dates");
        }

        // Update booking from CREATED to HELD using state handler (now counts in
        // inventory)
        BookingStateHandler.transition(booking, BookingStatus.HELD);
        // Set hold expiry time (15 minutes from now)
        long now = System.currentTimeMillis();
        booking.setHoldExpiresAt(now + 15 * 60 * 1000); // 15 minutes TTL
        bookingRepository.save(booking);

        // Create transaction
        String transactionId = UUID.randomUUID().toString();
        Transaction transaction = new Transaction(
                transactionId, bookingId, booking.getTotalAmountMinor(),
                "USD", TransactionStatus.PENDING,
                "PG_REF_" + transactionId, // TODO: Replace with actual PG provider reference
                System.currentTimeMillis(), null, null);

        transaction = transactionRepository.save(transaction);

        // TODO: Integrate with Payment Gateway (PG) to initiate payment
        // For now, we just create the transaction record
        // In real implementation, this would call PG API and get providerRef

        return transaction;
    }

    public void handleCallback(String providerRef, TransactionStatus status) {
        Optional<Transaction> transactionOpt = transactionRepository.findByProviderRef(providerRef);
        if (transactionOpt.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found for providerRef: " + providerRef);
        }

        Transaction transaction = transactionOpt.get();

        // Check if already processed (idempotency)
        if (transaction.getStatus() == TransactionStatus.COMPLETED ||
                transaction.getStatus() == TransactionStatus.FAILED) {
            return; // Already processed
        }

        Optional<Booking> bookingOpt = bookingRepository.findById(transaction.getBookingId());
        if (bookingOpt.isEmpty()) {
            throw new IllegalStateException("Booking not found for transaction");
        }

        Booking booking = bookingOpt.get();

        if (status == TransactionStatus.COMPLETED) {
            // Payment successful
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction.setCompletedAt(System.currentTimeMillis());
            BookingStateHandler.transition(booking, BookingStatus.CONFIRMED);
            booking.setPaymentStatus(TransactionStatus.COMPLETED);
            // TODO: Emit event for dashboard/receipt
        } else {
            // Payment failed
            transaction.setStatus(TransactionStatus.FAILED);
            BookingStateHandler.transition(booking, BookingStatus.CANCELLED);
            booking.setPaymentStatus(TransactionStatus.FAILED);
            // Inventory automatically restored since CANCELLED bookings don't count
        }

        transactionRepository.save(transaction);
        bookingRepository.save(booking);
    }

    public void issueRefund(String bookingId, long amountMinor) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }

        Booking booking = bookingOpt.get();

        // Find the transaction for this booking
        // TODO: Add method to find transaction by bookingId, or store transactionId in
        // booking
        // For now, we'll create a refund transaction
        String refundTransactionId = UUID.randomUUID().toString();
        Transaction refundTransaction = new Transaction(
                refundTransactionId, bookingId, amountMinor,
                "USD", TransactionStatus.REFUNDED,
                "REFUND_" + refundTransactionId,
                System.currentTimeMillis(), null, System.currentTimeMillis());

        transactionRepository.save(refundTransaction);

        // TODO: Integrate with Payment Gateway to process actual refund
    }
}

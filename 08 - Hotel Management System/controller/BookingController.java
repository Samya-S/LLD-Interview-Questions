package controller;

import domain.Booking;
import domain.DateRange;
import service.BookingService;

public class BookingController {
    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Booking createBooking(String userId, String hotelId, String roomTypeId, DateRange range,
            long expectedTotalPrice) {
        return bookingService.createBooking(userId, hotelId, roomTypeId, range, expectedTotalPrice);
    }

    public void cancelBooking(String bookingId, String userId) {
        bookingService.cancelBooking(bookingId, userId);
    }
}

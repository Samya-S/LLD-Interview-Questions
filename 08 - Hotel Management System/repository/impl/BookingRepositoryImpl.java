package repository.impl;

import domain.Booking;
import domain.BookingStatus;
import repository.BookingRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BookingRepositoryImpl implements BookingRepository {
    private Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    @Override
    public List<Booking> findByUser(String userId) {
        return bookings.values().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public int countConfirmedBookings(String hotelId, String roomTypeId, long dateUtc) {
        return (int) bookings.values().stream()
                .filter(booking -> booking.getHotelId().equals(hotelId))
                .filter(booking -> booking.getRoomTypeId().equals(roomTypeId))
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .filter(booking -> dateUtc >= booking.getCheckInDateUtc() && dateUtc < booking.getCheckOutDateUtc())
                .count();
    }

    @Override
    public int countHeldBookings(String hotelId, String roomTypeId, long dateUtc, long nowUtc) {
        return (int) bookings.values().stream()
                .filter(booking -> booking.getHotelId().equals(hotelId))
                .filter(booking -> booking.getRoomTypeId().equals(roomTypeId))
                .filter(booking -> booking.getBookingStatus() == BookingStatus.HELD)
                .filter(booking -> dateUtc >= booking.getCheckInDateUtc() && dateUtc < booking.getCheckOutDateUtc())
                .filter(booking -> booking.getHoldExpiresAt() > nowUtc) // not expired
                .count();
    }

    @Override
    public int countCheckedInBookings(String hotelId, String roomTypeId, long dateUtc) {
        return (int) bookings.values().stream()
                .filter(booking -> booking.getHotelId().equals(hotelId))
                .filter(booking -> booking.getRoomTypeId().equals(roomTypeId))
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CHECKED_IN)
                .filter(booking -> dateUtc >= booking.getCheckInDateUtc() && dateUtc < booking.getCheckOutDateUtc())
                .count();
    }
}

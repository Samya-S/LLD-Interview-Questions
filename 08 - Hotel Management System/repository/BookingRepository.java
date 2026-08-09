package repository;

import domain.Booking;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);

    Optional<Booking> findById(String bookingId);

    List<Booking> findByUser(String userId);

    int countConfirmedBookings(String hotelId, String roomTypeId, long dateUtc);

    int countHeldBookings(String hotelId, String roomTypeId, long dateUtc, long nowUtc);

    int countCheckedInBookings(String hotelId, String roomTypeId, long dateUtc);
    // TODO: Background Scheduler - Find expired HELD bookings
    // List<Booking> findExpiredHeldBookings(long nowUtc); // Find HELD bookings
    // where holdExpiresAt < nowUtc
}

package service;

import domain.DateRange;
import domain.Hotel;
import domain.RoomType;
import repository.BookingRepository;
import repository.HotelRepository;
import repository.RoomTypeRepository;
import java.util.Optional;

public class InventoryService {
    private BookingRepository bookingRepository;
    private HotelRepository hotelRepository;
    private RoomTypeRepository roomTypeRepository;

    public InventoryService(BookingRepository bookingRepository, HotelRepository hotelRepository,
            RoomTypeRepository roomTypeRepository) {
        this.bookingRepository = bookingRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public int getConfirmedBookingsCount(String hotelId, String roomTypeId, long dateUtc) {
        return bookingRepository.countConfirmedBookings(hotelId, roomTypeId, dateUtc);
    }

    public int getHeldBookingsCount(String hotelId, String roomTypeId, long dateUtc) {
        return bookingRepository.countHeldBookings(hotelId, roomTypeId, dateUtc, System.currentTimeMillis());
    }

    public int getCheckedInBookingsCount(String hotelId, String roomTypeId, long dateUtc) {
        return bookingRepository.countCheckedInBookings(hotelId, roomTypeId, dateUtc);
    }

    public boolean checkAvailability(String hotelId, String roomTypeId, DateRange range, int qty) {
        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(roomTypeId);
        if (roomTypeOpt.isEmpty()) {
            return false;
        }
        RoomType roomType = roomTypeOpt.get();

        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (hotelOpt.isEmpty()) {
            return false;
        }
        Hotel hotel = hotelOpt.get();

        int totalRooms = roomType.getTotalRooms();
        int overbookPercent = hotel.getDefaultOverbookPercent();
        int overbookAllowed = (int) Math.ceil((double) totalRooms * overbookPercent / 100.0);

        // Check availability for each night in the range
        for (long dateUtc = range.getCheckInDateUtc(); dateUtc < range.getCheckOutDateUtc(); dateUtc += 86400000) { // 24
                                                                                                                    // hours
                                                                                                                    // in
                                                                                                                    // ms
            int confirmedCount = getConfirmedBookingsCount(hotelId, roomTypeId, dateUtc);
            int heldCount = getHeldBookingsCount(hotelId, roomTypeId, dateUtc);
            int checkedInCount = getCheckedInBookingsCount(hotelId, roomTypeId, dateUtc);

            int available = totalRooms + overbookAllowed - confirmedCount - heldCount - checkedInCount;
            if (available < qty) {
                return false;
            }
        }
        return true;
    }
}

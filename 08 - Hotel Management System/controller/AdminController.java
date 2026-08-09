package controller;

import domain.*;
import service.BookingService;
import repository.HotelRepository;
import repository.RoomTypeRepository;
import repository.RoomRepository;
import repository.SeasonalPriceRepository;
import repository.CancellationPolicyRepository;
import java.util.Optional;

public class AdminController {
    private HotelRepository hotelRepository;
    private RoomTypeRepository roomTypeRepository;
    private RoomRepository roomRepository;
    private SeasonalPriceRepository seasonalPriceRepository;
    private CancellationPolicyRepository cancellationPolicyRepository;
    private BookingService bookingService;

    public AdminController(HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository, SeasonalPriceRepository seasonalPriceRepository,
            CancellationPolicyRepository cancellationPolicyRepository, BookingService bookingService) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.seasonalPriceRepository = seasonalPriceRepository;
        this.cancellationPolicyRepository = cancellationPolicyRepository;
        this.bookingService = bookingService;
    }

    public Hotel createOrUpdateHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public RoomType createOrUpdateRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    public void updateOverbookingPercent(String hotelId, String roomTypeId, int percent) {
        // TODO: Implement per-roomType overbooking override if needed
        // For now, we use hotel-level defaultOverbookPercent
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (hotelOpt.isPresent()) {
            Hotel hotel = hotelOpt.get();
            hotel.setDefaultOverbookPercent(percent);
            hotelRepository.save(hotel);
        }
    }

    public SeasonalPrice setSeasonalPrice(String hotelId, String roomTypeId, long dateUtc, long priceMinor) {
        Optional<SeasonalPrice> existingOpt = seasonalPriceRepository.findByKey(hotelId, roomTypeId, dateUtc);
        SeasonalPrice price;
        if (existingOpt.isPresent()) {
            price = existingOpt.get();
            price.setPriceMinor(priceMinor);
        } else {
            price = new SeasonalPrice(
                    java.util.UUID.randomUUID().toString(),
                    hotelId, roomTypeId, dateUtc, priceMinor, System.currentTimeMillis());
        }
        return seasonalPriceRepository.upsert(price);
    }

    public CancellationPolicy createOrUpdatePolicy(CancellationPolicy policy) {
        return cancellationPolicyRepository.save(policy);
    }

    public Booking checkIn(String bookingId, String roomId, long checkInTimeUtc) {
        return bookingService.checkIn(bookingId, roomId, checkInTimeUtc);
    }

    public Booking checkOut(String bookingId, long checkOutTimeUtc) {
        return bookingService.checkOut(bookingId, checkOutTimeUtc);
    }
}

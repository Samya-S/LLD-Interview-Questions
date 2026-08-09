package service;

import domain.*;
import repository.HotelRepository;
import repository.RoomTypeRepository;
import repository.RoomRepository;
import repository.BookingRepository;
import java.util.*;
import java.util.stream.Collectors;

public class SearchService {
    private HotelRepository hotelRepository;
    private RoomTypeRepository roomTypeRepository;
    private RoomRepository roomRepository;
    private PricingService pricingService;
    private InventoryService inventoryService;

    public SearchService(HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository,
            RoomRepository roomRepository, PricingService pricingService,
            BookingRepository bookingRepository) {
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.pricingService = pricingService;
        this.inventoryService = new InventoryService(bookingRepository, hotelRepository, roomTypeRepository);
    }

    public List<Hotel> searchHotels(SearchFilter filter) {
        List<Hotel> hotels;
        if (filter.getCity() != null && filter.getCountry() != null) {
            hotels = hotelRepository.findByLocation(filter.getCity(), filter.getCountry());
        } else {
            return new ArrayList<>();
        }
        // TODO: Implement full search with all filters (location, price range, etc.)
        // Implement caching and other things as well
        hotels = new ArrayList<>();
        return hotels;
    }

    public List<RoomTypeAvailability> getAvailability(String hotelId, DateRange range) {
        // Get hotel
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (hotelOpt.isEmpty()) {
            return Collections.emptyList();
        }
        Hotel hotel = hotelOpt.get();

        // Get all room types for this hotel
        List<RoomType> roomTypes = roomTypeRepository.findByHotel(hotelId);
        List<RoomTypeAvailability> result = new ArrayList<>();

        int overbookPercent = hotel.getDefaultOverbookPercent();

        // For each room type, check availability and pricing
        for (RoomType roomType : roomTypes) {
            if (!roomType.isActive()) {
                continue; // Skip inactive room types
            }

            String roomTypeId = roomType.getId();
            int totalRooms = roomType.getTotalRooms();
            int overbookAllowed = (int) Math.ceil((double) totalRooms * overbookPercent / 100.0);

            boolean available = true;
            int minAvailable = Integer.MAX_VALUE;

            // Check each night for availability
            for (long dateUtc = range.getCheckInDateUtc(); dateUtc < range.getCheckOutDateUtc(); dateUtc += 86400000) {
                int confirmedCount = inventoryService.getConfirmedBookingsCount(hotelId, roomTypeId, dateUtc);
                int heldCount = inventoryService.getHeldBookingsCount(hotelId, roomTypeId, dateUtc);
                int checkedInCount = inventoryService.getCheckedInBookingsCount(hotelId, roomTypeId, dateUtc);

                int availableForNight = totalRooms + overbookAllowed - confirmedCount - heldCount - checkedInCount;
                minAvailable = Math.min(minAvailable, availableForNight);
                if (availableForNight < 1) {
                    available = false;
                    break; // Not available, skip pricing calculation
                }
            }

            // Only include room types that are available
            if (available && minAvailable >= 1) {
                // Get prices
                List<NightlyPrice> nightlyPrices = pricingService.rateStay(hotelId, roomTypeId, range);
                long totalPrice = pricingService.computeTotal(nightlyPrices);
                int numberOfNights = (int) ((range.getCheckOutDateUtc() - range.getCheckInDateUtc()) / 86400000);
                double avgPrice = pricingService.computeAveragePricePerNight(nightlyPrices, numberOfNights);

                // Build RoomTypeAvailability with all details
                RoomTypeAvailability roomTypeAvailability = new RoomTypeAvailability(
                        roomType.getId(),
                        roomType.getName(),
                        roomType.getCapacity(),
                        roomType.getBedType(),
                        roomType.getAmenities(),
                        true, // available
                        totalPrice,
                        avgPrice,
                        nightlyPrices);
                result.add(roomTypeAvailability);
            }
        }

        return result;
    }
}

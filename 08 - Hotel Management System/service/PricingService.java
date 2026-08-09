package service;

import domain.DateRange;
import domain.NightlyPrice;
import domain.RoomType;
import domain.SeasonalPrice;
import repository.RoomTypeRepository;
import repository.SeasonalPriceRepository;
import java.util.*;

public class PricingService {
    private RoomTypeRepository roomTypeRepository;
    private SeasonalPriceRepository seasonalPriceRepository;

    public PricingService(RoomTypeRepository roomTypeRepository, SeasonalPriceRepository seasonalPriceRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.seasonalPriceRepository = seasonalPriceRepository;
    }

    public List<NightlyPrice> rateStay(String hotelId, String roomTypeId, DateRange range) {
        Optional<RoomType> roomTypeOpt = roomTypeRepository.findById(roomTypeId);
        if (roomTypeOpt.isEmpty()) {
            return Collections.emptyList();
        }
        RoomType roomType = roomTypeOpt.get();
        long basePrice = roomType.getBasePrice();

        List<NightlyPrice> nightlyPrices = new ArrayList<>();
        for (long dateUtc = range.getCheckInDateUtc(); dateUtc < range.getCheckOutDateUtc(); dateUtc += 86400000) {
            // Check for seasonal price, fallback to basePrice
            Optional<SeasonalPrice> seasonalPriceOpt = seasonalPriceRepository.findByKey(hotelId, roomTypeId, dateUtc);
            long price = seasonalPriceOpt.map(SeasonalPrice::getPriceMinor).orElse(basePrice);
            nightlyPrices.add(new NightlyPrice(dateUtc, price));
        }
        return nightlyPrices;
    }

    public long computeTotal(List<NightlyPrice> nightly) {
        return nightly.stream()
                .mapToLong(NightlyPrice::getPriceMinor)
                .sum();
    }

    public double computeAveragePricePerNight(List<NightlyPrice> nightly, int numberOfNights) {
        if (numberOfNights == 0) {
            return 0.0;
        }
        return (double) computeTotal(nightly) / numberOfNights;
    }
}

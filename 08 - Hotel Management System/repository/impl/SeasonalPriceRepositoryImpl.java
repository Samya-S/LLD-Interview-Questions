package repository.impl;

import domain.SeasonalPrice;
import repository.SeasonalPriceRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonalPriceRepositoryImpl implements SeasonalPriceRepository {
    // Key: hotelId:roomTypeId:dateUtc
    private Map<String, SeasonalPrice> prices = new ConcurrentHashMap<>();

    private String buildKey(String hotelId, String roomTypeId, long dateUtc) {
        return hotelId + ":" + roomTypeId + ":" + dateUtc;
    }

    @Override
    public SeasonalPrice upsert(SeasonalPrice price) {
        String key = buildKey(price.getHotelId(), price.getRoomTypeId(), price.getDateUtc());
        prices.put(key, price);
        return price;
    }

    @Override
    public Optional<SeasonalPrice> findByKey(String hotelId, String roomTypeId, long dateUtc) {
        String key = buildKey(hotelId, roomTypeId, dateUtc);
        return Optional.ofNullable(prices.get(key));
    }
}

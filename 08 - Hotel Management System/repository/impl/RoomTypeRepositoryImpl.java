package repository.impl;

import domain.RoomType;
import repository.RoomTypeRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoomTypeRepositoryImpl implements RoomTypeRepository {
    private Map<String, RoomType> roomTypes = new ConcurrentHashMap<>();

    @Override
    public RoomType save(RoomType roomType) {
        roomTypes.put(roomType.getId(), roomType);
        return roomType;
    }

    @Override
    public Optional<RoomType> findById(String roomTypeId) {
        return Optional.ofNullable(roomTypes.get(roomTypeId));
    }

    @Override
    public List<RoomType> findByHotel(String hotelId) {
        return roomTypes.values().stream()
                .filter(rt -> rt.getHotelId().equals(hotelId))
                .filter(RoomType::isActive)
                .collect(Collectors.toList());
    }
}

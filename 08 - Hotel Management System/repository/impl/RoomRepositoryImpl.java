package repository.impl;

import domain.Room;
import repository.RoomRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoomRepositoryImpl implements RoomRepository {
    private Map<String, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public Room save(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    @Override
    public List<Room> findByHotelAndType(String hotelId, String roomTypeId) {
        return rooms.values().stream()
                .filter(room -> room.getHotelId().equals(hotelId) && room.getRoomTypeId().equals(roomTypeId))
                .filter(Room::isActive)
                .collect(Collectors.toList());
    }
}

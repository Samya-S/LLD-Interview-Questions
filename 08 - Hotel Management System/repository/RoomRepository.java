package repository;

import domain.Room;
import java.util.List;

public interface RoomRepository {
    Room save(Room room);

    List<Room> findByHotelAndType(String hotelId, String roomTypeId);
}

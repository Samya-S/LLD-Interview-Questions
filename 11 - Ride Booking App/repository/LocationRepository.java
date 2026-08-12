package repository;

import domain.Location;

public interface LocationRepository {
    void saveLocation(String driverId, Location location);

    Location getLatestLocation(String driverId);
}
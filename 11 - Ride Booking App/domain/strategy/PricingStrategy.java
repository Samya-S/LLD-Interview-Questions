package domain.strategy;

import domain.Location;

public interface PricingStrategy {
    long calculateFare(Location pickup, Location dropoff, double distanceKm, long durationSec);
}
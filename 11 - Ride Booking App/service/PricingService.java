package service;

import domain.FareEstimateResponse;
import domain.Location;
import domain.strategy.PricingStrategy;

public class PricingService {
    private final PricingStrategy pricingStrategy;

    public PricingService(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public FareEstimateResponse estimateFare(Location pickup, Location dropoff, double distanceKm, long durationSec) {
        long fareMinor = pricingStrategy.calculateFare(pickup, dropoff, distanceKm, durationSec);
        return new FareEstimateResponse(fareMinor, distanceKm, durationSec, "USD");
    }
}
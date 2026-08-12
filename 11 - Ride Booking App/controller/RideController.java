package controller;

import domain.FareEstimateResponse;
import domain.Location;
import domain.Ride;
import domain.RideRequest;
import domain.RideStatusResponse;
import service.RideService;

public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    public FareEstimateResponse getFareEstimate(Location pickup, Location dropoff) {
        return rideService.estimateFare(pickup, dropoff);
    }

    public Ride requestRide(RideRequest request) {
        return rideService.requestRide(request);
    }

    public RideStatusResponse getRideStatus(String rideId) {
        return rideService.getRideStatus(rideId);
    }

    public void cancelRide(String rideId, String reason) {
        rideService.cancelRide(rideId, reason);
    }
}

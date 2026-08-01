package service;

import domain.Direction;
import domain.VehicleCounter;
import repository.TrafficRepository;

public class TrafficService {
    private TrafficRepository trafficRepository;

    public TrafficService(TrafficRepository trafficRepository) {
        this.trafficRepository = trafficRepository;
        System.out.println("TrafficService initialized");
    }

    public void updateVehicleCount(Direction direction, int count) {
        trafficRepository.updateCount(direction, count);
        System.out.println("Vehicle count updated for " + direction + ": " + count);
        
        // In a real implementation, this could trigger dynamic timing adjustments
        System.out.println("TODO: Trigger dynamic timing adjustment if enabled");
    }

    public int getVehicleCount(Direction direction) {
        int count = trafficRepository.getCount(direction);
        System.out.println("Vehicle count retrieved for " + direction + ": " + count);
        return count;
    }

    public void incrementVehicleCount(Direction direction) {
        trafficRepository.incrementCount(direction);
        System.out.println("Vehicle count incremented for " + direction);
    }

    public void resetVehicleCount(Direction direction) {
        trafficRepository.resetCount(direction);
        System.out.println("Vehicle count reset for " + direction);
    }

    public VehicleCounter getVehicleCounter(Direction direction) {
        return trafficRepository.getCounter(direction);
    }

    public void detectTrafficCondition(Direction direction) {
        int count = trafficRepository.getCount(direction);
        if (count > 10) {
            System.out.println("High traffic detected for " + direction + ": " + count + " vehicles");
            System.out.println("TODO: Trigger dynamic timing adjustment");
        } else if (count == 0) {
            System.out.println("No traffic detected for " + direction);
            System.out.println("TODO: Consider reducing green time for this direction");
        }
    }
} 
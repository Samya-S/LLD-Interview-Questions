package service;

import domain.Direction;
import domain.SignalTiming;
import repository.TimingRepository;

public class TimingService {
    private TimingRepository timingRepository;
    private TrafficService trafficService;

    public TimingService(TimingRepository timingRepository, TrafficService trafficService) {
        this.timingRepository = timingRepository;
        this.trafficService = trafficService;
        System.out.println("TimingService initialized");
    }

    public void setSignalTiming(int intersectionId, Direction direction, int greenDuration) {
        // Validate timing parameters
        if (greenDuration < 10) {
            System.out.println("Invalid timing parameters: minimum green duration not met (10s)");
            return;
        }
        
        timingRepository.updateSignalTiming(intersectionId, direction, greenDuration);
        System.out.println("Signal timing set for intersection " + intersectionId + 
                          ", direction " + direction + 
                          ": Y=" + SignalTiming.YELLOW_DURATION + "s, G=" + greenDuration + "s");
    }

    public void enableDynamicTiming(int intersectionId, Direction direction, boolean enable) {
        timingRepository.enableDynamicTiming(intersectionId, direction, enable);
        System.out.println("Dynamic timing " + (enable ? "enabled" : "disabled") + 
                          " for intersection " + intersectionId + ", direction " + direction);
    }

    public SignalTiming getSignalTiming(int intersectionId, Direction direction) {
        SignalTiming timing = timingRepository.getSignalTiming(intersectionId, direction);
        if (timing != null) {
            System.out.println("Signal timing retrieved for intersection " + intersectionId + 
                              ", direction " + direction);
        }
        return timing;
    }

    public void adjustTimingBasedOnTraffic(int intersectionId, Direction direction) {
        // TODO: FUTURE ENHANCEMENT - Implement dynamic timing adjustment
        // This method should:
        // 1. Get current vehicle count for the direction
        // 2. Calculate optimal green duration based on traffic density
        // 3. Update signal timing if significantly different from current
        // 4. Apply changes to next cycle (not current cycle)
        // 5. Consider safety constraints (min/max durations)
        // 6. Log timing changes for analysis
        System.out.println("TODO: Dynamic timing adjustment not yet implemented for intersection " + 
                          intersectionId + ", direction " + direction);
    }

    public int calculateOptimalGreenDuration(int vehicleCount) {
        // TODO: FUTURE ENHANCEMENT - Implement sophisticated timing algorithm
        // This method should:
        // 1. Use machine learning models for traffic prediction
        // 2. Consider historical traffic patterns
        // 3. Factor in time of day, weather, events
        // 4. Balance throughput vs wait times
        // 5. Ensure safety constraints are met
        System.out.println("TODO: Optimal duration calculation not yet implemented for " + vehicleCount + " vehicles");
        return 30; // Default fallback duration
    }

    public void validateTiming(int greenDuration) {
        if (greenDuration < 10) {
            System.out.println("Warning: Green duration too short (minimum 10s)");
        }
        if (greenDuration > 120) {
            System.out.println("Warning: Green duration too long (maximum 120s)");
        }
        
        System.out.println("Timing validation completed");
    }
} 
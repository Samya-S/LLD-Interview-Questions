package controller;

import domain.Direction;
import domain.SignalTiming;
import service.TimingService;

public class TimingController {
    private TimingService timingService;

    public TimingController(TimingService timingService) {
        this.timingService = timingService;
        System.out.println("TimingController initialized");
    }

    public void setSignalTiming(int intersectionId, Direction direction, int greenDuration) {
        System.out.println("Setting signal timing for intersection " + intersectionId + 
                          ", direction " + direction + 
                          ": Y=" + SignalTiming.YELLOW_DURATION + "s, G=" + greenDuration + "s");
        timingService.setSignalTiming(intersectionId, direction, greenDuration);
    }

    public void enableDynamicTiming(int intersectionId, Direction direction, boolean enable) {
        System.out.println("Enabling dynamic timing for intersection " + intersectionId + 
                          ", direction " + direction + ": " + enable);
        timingService.enableDynamicTiming(intersectionId, direction, enable);
    }

    public SignalTiming getSignalTiming(int intersectionId, Direction direction) {
        System.out.println("Getting signal timing for intersection " + intersectionId + 
                          ", direction " + direction);
        return timingService.getSignalTiming(intersectionId, direction);
    }

    public void adjustTimingBasedOnTraffic(int intersectionId, Direction direction) {
        System.out.println("Adjusting timing based on traffic for intersection " + intersectionId + 
                          ", direction " + direction);
        timingService.adjustTimingBasedOnTraffic(intersectionId, direction);
        // TODO: FUTURE ENHANCEMENT - This will be implemented in future versions
    }

    public void displayTimingStatus(int intersectionId) {
        System.out.println("=== Timing Status for Intersection " + intersectionId + " ===");
        for (Direction direction : Direction.values()) {
            SignalTiming timing = timingService.getSignalTiming(intersectionId, direction);
            if (timing != null) {
                System.out.println(direction + ": Y=" + timing.getYellowDuration() + "s, " +
                                 "G=" + timing.getGreenDuration() + "s, " +
                                 "Dynamic=" + timing.isDynamic());
            } else {
                System.out.println(direction + ": No timing configured");
            }
        }
        System.out.println("===============================================");
    }

    public void validateTiming(int greenDuration) {
        System.out.println("Validating timing parameters: Y=" + SignalTiming.YELLOW_DURATION + "s, G=" + greenDuration + "s");
        timingService.validateTiming(greenDuration);
    }

    public int calculateOptimalGreenDuration(int vehicleCount) {
        System.out.println("Calculating optimal green duration for " + vehicleCount + " vehicles");
        return timingService.calculateOptimalGreenDuration(vehicleCount);
    }
} 
package repository;

import java.util.HashMap;
import java.util.Map;
import domain.Direction;
import domain.SignalTiming;

public class TimingRepository {
    private Map<String, SignalTiming> signalTimings; // Key: intersectionId-direction

    public TimingRepository() {
        this.signalTimings = new HashMap<>();
        System.out.println("TimingRepository initialized");
    }

    public void saveSignalTiming(SignalTiming timing) {
        String key = timing.getIntersectionId() + "-" + timing.getDirection();
        signalTimings.put(key, timing);
        System.out.println("Signal timing saved for intersection " + timing.getIntersectionId() + 
                          ", direction " + timing.getDirection());
    }

    public SignalTiming getSignalTiming(int intersectionId, Direction direction) {
        String key = intersectionId + "-" + direction;
        SignalTiming timing = signalTimings.get(key);
        if (timing != null) {
            System.out.println("Signal timing found for intersection " + intersectionId + 
                              ", direction " + direction);
        } else {
            System.out.println("Signal timing not found for intersection " + intersectionId + 
                              ", direction " + direction);
        }
        return timing;
    }

    public void updateSignalTiming(int intersectionId, Direction direction, int greenDuration) {
        String key = intersectionId + "-" + direction;
        SignalTiming timing = signalTimings.get(key);
        if (timing != null) {
            timing.updateTiming(greenDuration);
            System.out.println("Signal timing updated for intersection " + intersectionId + 
                              ", direction " + direction);
        } else {
            System.out.println("Signal timing not found for intersection " + intersectionId + 
                              ", direction " + direction);
        }
    }

    public void enableDynamicTiming(int intersectionId, Direction direction, boolean enable) {
        String key = intersectionId + "-" + direction;
        SignalTiming timing = signalTimings.get(key);
        if (timing != null) {
            timing.setDynamic(enable);
            System.out.println("Dynamic timing " + (enable ? "enabled" : "disabled") + 
                              " for intersection " + intersectionId + ", direction " + direction);
        } else {
            System.out.println("Signal timing not found for intersection " + intersectionId + 
                              ", direction " + direction);
        }
    }

    public void initializeDefaultTimings(int intersectionId) {
        for (Direction direction : Direction.values()) {
            SignalTiming timing = new SignalTiming(intersectionId, direction);
            saveSignalTiming(timing);
        }
        System.out.println("Default timings initialized for intersection: " + intersectionId);
    }
} 
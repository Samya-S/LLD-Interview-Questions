package domain;

import java.util.HashMap;
import java.util.Map;

public class Intersection {
    private int id;
    private String name;
    private Map<Direction, TrafficLight> trafficLights;
    private boolean isEmergencyMode;
    private Direction emergencyDirection;
    private boolean isCyclePaused;

    public Intersection(int id, String name) {
        this.id = id;
        this.name = name;
        this.trafficLights = new HashMap<>();
        this.isEmergencyMode = false;
        this.isCyclePaused = false;
        for (Direction direction : Direction.values()) {
            this.trafficLights.put(direction, new TrafficLight(direction));
        }
        System.out.println("Intersection created: " + name + " (ID: " + id + ")");
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Map<Direction, TrafficLight> getTrafficLights() { return trafficLights; }
    public boolean isEmergencyMode() { return isEmergencyMode; }
    public Direction getEmergencyDirection() { return emergencyDirection; }
    public boolean isCyclePaused() { return isCyclePaused; }

    public void setEmergencyMode(boolean emergencyMode) {
        this.isEmergencyMode = emergencyMode;
        System.out.println("Emergency mode " + (emergencyMode ? "enabled" : "disabled") + " for intersection " + id);
    }

    public void setEmergencyDirection(Direction emergencyDirection) {
        this.emergencyDirection = emergencyDirection;
        System.out.println("Emergency direction set to: " + emergencyDirection + " for intersection " + id);
    }

    public void setCyclePaused(boolean cyclePaused) {
        this.isCyclePaused = cyclePaused;
        System.out.println("Cycle " + (cyclePaused ? "paused" : "resumed") + " for intersection " + id);
    }

    public TrafficLight getTrafficLight(Direction direction) {
        return trafficLights.get(direction);
    }

    public void setAllSignalsToRed() {
        for (TrafficLight light : trafficLights.values()) {
            String currentState = light.getCurrentStateName();
            if ("GREEN".equals(currentState)) {
                // GREEN → YELLOW → RED (proper sequence)
                light.turnYellow();
                // In a real system, we'd wait for yellow duration, then turn red
                // For simulation, we'll turn red immediately after yellow
                light.turnRed();
            } else if ("YELLOW".equals(currentState)) {
                // YELLOW → RED
                light.turnRed();
            } else if ("RED".equals(currentState)) {
                // Already RED, no change needed
                System.out.println("Traffic light " + light.getDirection() + " is already RED");
            } else {
                // OFF or any other state → RED
                light.turnRed();
            }
        }
        System.out.println("All signals set to RED for intersection " + id);
    }

    public void setSignalToGreen(Direction direction) {
        TrafficLight light = trafficLights.get(direction);
        if (light != null) {
            light.turnGreen();
            System.out.println("Signal " + direction + " set to GREEN for intersection " + id);
        }
    }
    
    public void setSignalToYellow(Direction direction) {
        TrafficLight light = trafficLights.get(direction);
        if (light != null) {
            light.turnYellow();
            System.out.println("Signal " + direction + " set to YELLOW for intersection " + id);
        }
    }
    
    public void setSignalToRed(Direction direction) {
        TrafficLight light = trafficLights.get(direction);
        if (light != null) {
            light.turnRed();
            System.out.println("Signal " + direction + " set to RED for intersection " + id);
        }
    }
    
    public void setSignalToOff(Direction direction) {
        TrafficLight light = trafficLights.get(direction);
        if (light != null) {
            light.turnOff();
            System.out.println("Signal " + direction + " set to OFF for intersection " + id);
        }
    }
    
    /**
     * Emergency method to safely transition a signal to RED following proper state sequence
     */
    public void emergencyTransitionToRed(Direction direction) {
        TrafficLight light = trafficLights.get(direction);
        if (light != null) {
            String currentState = light.getCurrentStateName();
            if ("GREEN".equals(currentState)) {
                // GREEN → YELLOW → RED (emergency sequence)
                System.out.println("Emergency transition: " + direction + " GREEN → YELLOW → RED");
                light.turnYellow();
                light.turnRed();
            } else if ("YELLOW".equals(currentState)) {
                // YELLOW → RED
                System.out.println("Emergency transition: " + direction + " YELLOW → RED");
                light.turnRed();
            } else if ("RED".equals(currentState)) {
                // Already RED
                System.out.println("Emergency transition: " + direction + " already RED");
            } else {
                // OFF or any other state → RED
                System.out.println("Emergency transition: " + direction + " → RED");
                light.turnRed();
            }
        }
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", emergencyMode=" + isEmergencyMode +
                ", cyclePaused=" + isCyclePaused +
                '}';
    }
} 
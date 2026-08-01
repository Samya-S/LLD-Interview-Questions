package repository;

import java.util.HashMap;
import java.util.Map;
import domain.Intersection;
import domain.IntersectionCycle;
import domain.Direction;

public class IntersectionRepository {
    private Map<Integer, Intersection> intersections;
    private Map<Integer, IntersectionCycle> cycles;
    private int nextId = 1;

    public IntersectionRepository() {
        this.intersections = new HashMap<>();
        this.cycles = new HashMap<>();
        System.out.println("IntersectionRepository initialized");
    }

    public void save(Intersection intersection) {
        intersections.put(intersection.getId(), intersection);
        System.out.println("Intersection saved: " + intersection.getId());
    }

    public Intersection findById(int intersectionId) {
        Intersection intersection = intersections.get(intersectionId);
        if (intersection != null) {
            System.out.println("Intersection found: " + intersectionId);
        } else {
            System.out.println("Intersection not found: " + intersectionId);
        }
        return intersection;
    }

    public void updateCycle(int intersectionId, IntersectionCycle cycle) {
        cycles.put(intersectionId, cycle);
        System.out.println("Cycle updated for intersection: " + intersectionId);
    }

    public IntersectionCycle getCycle(int intersectionId) {
        IntersectionCycle cycle = cycles.get(intersectionId);
        if (cycle != null) {
            System.out.println("Cycle found for intersection: " + intersectionId);
        } else {
            System.out.println("Cycle not found for intersection: " + intersectionId);
        }
        return cycle;
    }

    public void updateEmergencyMode(int intersectionId, boolean emergencyMode, Direction direction) {
        Intersection intersection = intersections.get(intersectionId);
        if (intersection != null) {
            intersection.setEmergencyMode(emergencyMode);
            intersection.setEmergencyDirection(direction);
            System.out.println("Emergency mode updated for intersection: " + intersectionId);
        }
    }

    public int getNextId() {
        return nextId++;
    }

    public boolean exists(int intersectionId) {
        return intersections.containsKey(intersectionId);
    }
} 
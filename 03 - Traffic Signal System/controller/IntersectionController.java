package controller;

import domain.Direction;
import domain.Intersection;
import domain.IntersectionCycle;
import domain.TrafficLight;
import service.IntersectionService;

public class IntersectionController {
    private IntersectionService intersectionService;

    public IntersectionController(IntersectionService intersectionService) {
        this.intersectionService = intersectionService;
        System.out.println("IntersectionController initialized");
    }

    public void createIntersection(int id, String name) {
        System.out.println("Creating intersection: " + name + " (ID: " + id + ")");
        intersectionService.createIntersection(id, name);
    }

    public Intersection getIntersection(int intersectionId) {
        System.out.println("Getting intersection: " + intersectionId);
        return intersectionService.getIntersection(intersectionId);
    }

    public void startCycle(int intersectionId) {
        System.out.println("Starting cycle for intersection: " + intersectionId);
        intersectionService.startAutomaticCycle(intersectionId);
    }

    public void displayStatus(int intersectionId) {
        System.out.println("Displaying status for intersection: " + intersectionId);
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        if (intersection != null) {
            System.out.println("=== Intersection Status ===");
            System.out.println("ID: " + intersection.getId());
            System.out.println("Name: " + intersection.getName());
            System.out.println("Emergency Mode: " + intersection.isEmergencyMode());
            System.out.println("Cycle Paused: " + intersection.isCyclePaused());
            System.out.println("Paused Phase: " + intersectionService.getPausedPhase(intersectionId));
            
            System.out.println("Traffic Light States:");
            for (Direction direction : Direction.values()) {
                TrafficLight light = intersection.getTrafficLight(direction);
                System.out.println("  " + direction + ": " + light.getCurrentStateName());
            }
            
            IntersectionCycle cycle = intersectionService.getCycle(intersectionId);
            if (cycle != null) {
                System.out.println("Current Phase: " + cycle.getCurrentPhase());
                System.out.println("Phase Start Time: " + cycle.getPhaseStartTime());
            }
            System.out.println("========================");
        } else {
            System.out.println("Intersection not found: " + intersectionId);
        }
    }
} 
package service;

import domain.Intersection;
import domain.IntersectionCycle;
import domain.Direction;

import repository.IntersectionRepository;
import repository.TimingRepository;

public class IntersectionService {
    private IntersectionRepository intersectionRepository;
    private TimingRepository timingRepository;

    public IntersectionService(IntersectionRepository intersectionRepository, TimingRepository timingRepository) {
        this.intersectionRepository = intersectionRepository;
        this.timingRepository = timingRepository;
        System.out.println("IntersectionService initialized");
    }

    public void createIntersection(int id, String name) {
        if (intersectionRepository.exists(id)) {
            System.out.println("Intersection with ID " + id + " already exists");
            return;
        }
        
        Intersection intersection = new Intersection(id, name);
        intersectionRepository.save(intersection);
        
        // Initialize default signal timings
        timingRepository.initializeDefaultTimings(id);
        
        // Create cycle for the intersection
        IntersectionCycle cycle = new IntersectionCycle(id);
        intersectionRepository.updateCycle(id, cycle);

        startAutomaticCycle(id);
        System.out.println("Intersection created successfully: " + name + " (ID: " + id + ")");
    }

    public Intersection getIntersection(int intersectionId) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection == null) {
            System.out.println("Intersection not found: " + intersectionId);
        }
        return intersection;
    }

    public void startAutomaticCycle(int intersectionId) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection == null) {
            System.out.println("Cannot start cycle: Intersection not found: " + intersectionId);
            return;
        }
        
        IntersectionCycle cycle = intersectionRepository.getCycle(intersectionId);
        if (cycle == null) {
            cycle = new IntersectionCycle(intersectionId);
            intersectionRepository.updateCycle(intersectionId, cycle);
        }
        
        cycle.setPaused(false);
        intersection.setCyclePaused(false);
        
        System.out.println("Automatic cycle started for intersection: " + intersectionId);
        // In a real implementation, this would start a timer/scheduler
        System.out.println("TODO: Implement timer-based automatic cycling");
    }

    public void pauseCycle(int intersectionId) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection == null) {
            System.out.println("Cannot pause cycle: Intersection not found: " + intersectionId);
            return;
        }
        
        IntersectionCycle cycle = intersectionRepository.getCycle(intersectionId);
        if (cycle != null) {
            cycle.setPaused(true);
            intersection.setCyclePaused(true);
            // intersection.setEmergencyDirection(....);
            System.out.println("Cycle paused for intersection: " + intersectionId);
        }
    }

    public void resumeCycle(int intersectionId) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection == null) {
            System.out.println("Cannot resume cycle: Intersection not found: " + intersectionId);
            return;
        }
        
        IntersectionCycle cycle = intersectionRepository.getCycle(intersectionId);
        if (cycle != null) {
            cycle.setPaused(false);
            intersection.setCyclePaused(false);
            System.out.println("Cycle resumed for intersection: " + intersectionId);
        }
    }

    public IntersectionCycle getCycle(int intersectionId) {
        return intersectionRepository.getCycle(intersectionId);
    }

    public void setAllSignalsToRed(int intersectionId) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection != null) {
            intersection.setAllSignalsToRed();
        }
    }
    
    public void emergencySetAllSignalsToRed(int intersectionId) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection != null) {
            // Use emergency transition for each direction
            for (domain.Direction direction : domain.Direction.values()) {
                intersection.emergencyTransitionToRed(direction);
            }
            System.out.println("Emergency: All signals transitioned to RED for intersection " + intersectionId);
        }
    }

    public void setSignalToGreen(int intersectionId, Direction direction) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection != null) {
            intersection.setSignalToGreen(direction);
        }
    }
    
    public void setSignalToYellow(int intersectionId, Direction direction) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection != null) {
            intersection.setSignalToYellow(direction);
        }
    }
    
    public void setSignalToRed(int intersectionId, Direction direction) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection != null) {
            intersection.setSignalToRed(direction);
        }
    }
    
    public void setSignalToOff(int intersectionId, Direction direction) {
        Intersection intersection = intersectionRepository.findById(intersectionId);
        if (intersection != null) {
            intersection.setSignalToOff(direction);
        }
    }

    public int getPausedPhase(int intersectionId) {
        IntersectionCycle cycle = intersectionRepository.getCycle(intersectionId);
        return cycle != null ? cycle.getPausedAtPhase() : 0;
    }
} 
package service;

import domain.Direction;
import domain.EmergencyRequest;
import domain.Intersection;

import repository.EmergencyRepository;

public class EmergencyService {
    private EmergencyRepository emergencyRepository;
    private IntersectionService intersectionService;

    public EmergencyService(EmergencyRepository emergencyRepository, IntersectionService intersectionService) {
        this.emergencyRepository = emergencyRepository;
        this.intersectionService = intersectionService;
        System.out.println("EmergencyService initialized");
    }

    public void requestEmergency(int intersectionId, Direction direction, int duration) {
        // Check if intersection exists
        if (intersectionService.getIntersection(intersectionId) == null) {
            System.out.println("Cannot request emergency: Intersection not found: " + intersectionId);
            return;
        }
        
        // Create emergency request
        int requestId = emergencyRepository.getNextId();
        EmergencyRequest request = new EmergencyRequest(requestId, intersectionId, direction, duration);
        emergencyRepository.save(request);
        
        // Pause the automatic cycle
        intersectionService.pauseCycle(intersectionId);
        
        // Set all signals to RED using emergency transition
        intersectionService.emergencySetAllSignalsToRed(intersectionId);
        
        // Set emergency direction to GREEN
        intersectionService.setSignalToGreen(intersectionId, direction);
        
        // Update intersection emergency mode
        intersectionService.getIntersection(intersectionId).setEmergencyMode(true);
        intersectionService.getIntersection(intersectionId).setEmergencyDirection(direction);
        
        System.out.println("Emergency request processed: " + requestId + 
                          " for intersection " + intersectionId + 
                          ", direction " + direction + 
                          ", duration " + duration + "s");
        
        // In a real implementation, this would schedule the emergency to end after duration
        System.out.println("TODO: Schedule emergency end after " + duration + " seconds");
    }

    public void endEmergency(int intersectionId) {
        EmergencyRequest activeEmergency = emergencyRepository.getActiveEmergency(intersectionId);
        if (activeEmergency == null) {
            System.out.println("No active emergency found for intersection: " + intersectionId);
            return;
        }
        
        // Set all signals to RED using emergency transition
        intersectionService.emergencySetAllSignalsToRed(intersectionId);
        
        // Deactivate emergency request
        emergencyRepository.updateStatus(activeEmergency.getId(), false);
        
        // Update intersection emergency mode
        Intersection intersection = intersectionService.getIntersection(intersectionId);
        if (intersection != null) {
            intersection.setEmergencyMode(false);
            intersection.setEmergencyDirection(null);
        }
        
        // Resume the cycle from where it was paused
        intersectionService.resumeCycle(intersectionId);
        
        System.out.println("Emergency ended for intersection: " + intersectionId);
    }

    public EmergencyRequest getActiveEmergency(int intersectionId) {
        return emergencyRepository.getActiveEmergency(intersectionId);
    }

    public void cleanupExpiredEmergencies() {
        emergencyRepository.removeExpiredRequests();
        System.out.println("Expired emergency requests cleaned up");
    }
} 
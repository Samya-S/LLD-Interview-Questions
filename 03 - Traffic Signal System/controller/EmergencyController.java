package controller;

import domain.Direction;
import domain.EmergencyRequest;
import service.EmergencyService;

public class EmergencyController {
    private EmergencyService emergencyService;

    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
        System.out.println("EmergencyController initialized");
    }

    public void requestEmergency(int intersectionId, Direction direction, int duration) {
        System.out.println("Emergency request received for intersection: " + intersectionId + 
                          ", direction: " + direction + ", duration: " + duration + "s");
        emergencyService.requestEmergency(intersectionId, direction, duration);
    }

    public void endEmergency(int intersectionId) {
        System.out.println("Ending emergency for intersection: " + intersectionId);
        emergencyService.endEmergency(intersectionId);
    }

    public void getEmergencyStatus(int intersectionId) {
        System.out.println("Getting emergency status for intersection: " + intersectionId);
        EmergencyRequest activeEmergency = emergencyService.getActiveEmergency(intersectionId);
        if (activeEmergency != null) {
            System.out.println("=== Emergency Status ===");
            System.out.println("Emergency ID: " + activeEmergency.getId());
            System.out.println("Direction: " + activeEmergency.getDirection());
            System.out.println("Duration: " + activeEmergency.getDuration() + "s");
            System.out.println("Active: " + activeEmergency.isActive());
            System.out.println("Request Time: " + activeEmergency.getRequestTime());
            System.out.println("Expired: " + activeEmergency.isExpired());
            System.out.println("=======================");
        } else {
            System.out.println("No active emergency for intersection: " + intersectionId);
        }
    }

    public void cleanupExpiredEmergencies() {
        System.out.println("Cleaning up expired emergency requests");
        emergencyService.cleanupExpiredEmergencies();
    }
} 
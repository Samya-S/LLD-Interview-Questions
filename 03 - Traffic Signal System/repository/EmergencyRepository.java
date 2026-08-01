package repository;

import java.util.HashMap;
import java.util.Map;
import domain.EmergencyRequest;

public class EmergencyRepository {
    private Map<Integer, EmergencyRequest> emergencyRequests;
    private int nextId = 1;

    public EmergencyRepository() {
        this.emergencyRequests = new HashMap<>();
        System.out.println("EmergencyRepository initialized");
    }

    public void save(EmergencyRequest request) {
        emergencyRequests.put(request.getId(), request);
        System.out.println("Emergency request saved: " + request.getId());
    }

    public EmergencyRequest getActiveEmergency(int intersectionId) {
        for (EmergencyRequest request : emergencyRequests.values()) {
            if (request.getIntersectionId() == intersectionId && request.isActive()) {
                System.out.println("Active emergency found for intersection: " + intersectionId);
                return request;
            }
        }
        System.out.println("No active emergency found for intersection: " + intersectionId);
        return null;
    }

    public void updateStatus(int requestId, boolean isActive) {
        EmergencyRequest request = emergencyRequests.get(requestId);
        if (request != null) {
            request.setActive(isActive);
            System.out.println("Emergency request status updated: " + requestId + " -> " + isActive);
        } else {
            System.out.println("Emergency request not found: " + requestId);
        }
    }

    public int getNextId() {
        return nextId++;
    }

    public void removeExpiredRequests() {
        emergencyRequests.entrySet().removeIf(entry -> entry.getValue().isExpired());
        System.out.println("Expired emergency requests removed");
    }
} 
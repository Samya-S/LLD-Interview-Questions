package service;

import domain.Elevator;
import domain.ElevatorState;
import repository.ElevatorRepository;
import repository.impl.ElevatorRepositoryImpl;
import java.util.List;

public class ElevatorService {
    private final ElevatorRepository elevatorRepository;
    private RequestService requestService; // Will be injected
    
    public ElevatorService() {
        this.elevatorRepository = new ElevatorRepositoryImpl();
    }
    
    // Setter for dependency injection
    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
    }
    
    public Elevator createElevator(String buildingId, int capacity) {
        Elevator elevator = new Elevator(buildingId, capacity);
        return elevatorRepository.save(elevator);
    }
    
    public void updateElevatorState(String elevatorId, ElevatorState state) {
        elevatorRepository.findById(elevatorId).ifPresent(elevator -> {
            elevator.setState(state);
            elevatorRepository.save(elevator);
        });
    }
    
    public void updateElevatorFloor(String elevatorId, int floor) {
        elevatorRepository.findById(elevatorId).ifPresent(elevator -> {
            elevator.setCurrentFloor(floor);
            elevatorRepository.save(elevator);
        });
    }
    
    public List<Elevator> getAvailableElevators(String buildingId) {
        return elevatorRepository.findAvailableElevators(buildingId);
    }
    
    public List<Elevator> getAllElevators(String buildingId) {
        return elevatorRepository.findByBuilding(buildingId);
    }
    
    public Elevator findById(String elevatorId) {
        return elevatorRepository.findById(elevatorId).orElse(null);
    }
    
    public void setMaintenanceMode(String elevatorId, boolean maintenance) {
        elevatorRepository.findById(elevatorId).ifPresent(elevator -> {
            if (maintenance) {
                // Always use graceful degradation - go to pre-maintenance first
                if (hasPendingRequests(elevatorId) || hasAssignedRequests(elevatorId)) {
                    elevator.setStateHandler(new domain.state.PreMaintenanceState());
                    System.out.println("Elevator " + elevatorId + " entering pre-maintenance mode to complete pending requests gracefully");
                } else {
                    elevator.enterMaintenance(); // Direct to maintenance if no requests at all
                    System.out.println("Elevator " + elevatorId + " entering maintenance mode directly (no pending requests)");
                }
            } else {
                elevator.exitMaintenance(); // Use state pattern method
            }
            elevatorRepository.save(elevator);
        });
    }
    
    private boolean hasPendingRequests(String elevatorId) {
        if (requestService == null) {
            System.out.println("Warning: RequestService not injected, cannot check pending requests");
            return false;
        }
        List<domain.InternalRequest> pendingRequests = requestService.getPendingRequestsForElevator(elevatorId);
        return !pendingRequests.isEmpty();
    }
    
    private boolean hasAssignedRequests(String elevatorId) {
        if (requestService == null) {
            System.out.println("Warning: RequestService not injected, cannot check assigned requests");
            return false;
        }
        List<domain.ExternalRequest> assignedRequests = requestService.getAssignedRequestsForElevator(elevatorId);
        return !assignedRequests.isEmpty();
    }
    
    public List<Elevator> getElevatorsByBuilding(String buildingId) {
        return elevatorRepository.findByBuilding(buildingId);
    }
    
    public void setPreMaintenanceMode(String elevatorId) {
        elevatorRepository.findById(elevatorId).ifPresent(elevator -> {
            elevator.setStateHandler(new domain.state.PreMaintenanceState());
            elevatorRepository.save(elevator);
            System.out.println("Elevator " + elevatorId + " set to pre-maintenance mode");
        });
    }
    
    public void checkMaintenanceTransition(String elevatorId) {
        elevatorRepository.findById(elevatorId).ifPresent(elevator -> {
            if (elevator.getStateHandler() instanceof domain.state.PreMaintenanceState) {
                if (!hasPendingRequests(elevatorId)) {
                    elevator.enterMaintenance();
                    elevatorRepository.save(elevator);
                    System.out.println("Elevator " + elevatorId + " transitioned to maintenance mode");
                }
            }
        });
    }
}

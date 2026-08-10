package controller;

import domain.Elevator;
import service.*;

public class ElevatorController {
    private final ElevatorService elevatorService;
    private final BuildingService buildingService;
    private final RequestService requestService;
    private final DispatcherService dispatcherService;
    private final MovementService movementService;
    
    public ElevatorController() {
        this.elevatorService = new ElevatorService();
        this.buildingService = new BuildingService();
        this.requestService = new RequestService();
        this.dispatcherService = new DispatcherService(requestService, elevatorService);
        this.movementService = new MovementService(elevatorService, requestService, buildingService, dispatcherService);
        
        // Inject RequestService into ElevatorService for proper dependency
        this.elevatorService.setRequestService(requestService);
    }
    
    public Elevator createElevator(String buildingId, int capacity) {
        if (!buildingService.buildingExists(buildingId)) {
            throw new IllegalArgumentException("Building not found: " + buildingId);
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Elevator capacity must be positive: " + capacity);
        }
        return elevatorService.createElevator(buildingId, capacity);
    }
    
    public void moveElevator(String elevatorId, int targetFloor) {
        Elevator elevator = elevatorService.findById(elevatorId);
        if (elevator == null) {
            throw new IllegalArgumentException("Elevator not found");
        }
        
        if (!buildingService.isValidFloor(elevator.getBuildingId(), targetFloor)) {
            throw new IllegalArgumentException("Invalid floor number");
        }
        
        // Create internal request for movement
        requestService.createInternalRequest(elevatorId, targetFloor);
        System.out.println("Move request created for elevator " + elevatorId + " to floor " + targetFloor);
    }
    
    public void setElevatorMaintenance(String elevatorId, boolean maintenance) {
        elevatorService.setMaintenanceMode(elevatorId, maintenance);
        System.out.println("Elevator " + elevatorId + " maintenance mode: " + maintenance);
    }
    
    public void startElevatorSystem(String buildingId) {
        if (!buildingService.isSystemRunning(buildingId)) {
            movementService.startElevatorSystem(buildingId);
            System.out.println("Elevator system started for building: " + buildingId);
        } else {
            System.out.println("Elevator system is already running");
        }
    }
    
    public void stopElevatorSystem(String buildingId) {
        if (buildingService.isSystemRunning(buildingId)) {
            movementService.stopElevatorSystem(buildingId);
            System.out.println("Elevator system stopped for building: " + buildingId);
        } else {
            System.out.println("Elevator system is not running");
        }
    }
    
    // Getters for other controllers to access services
    public DispatcherService getDispatcherService() { return dispatcherService; }
    public RequestService getRequestService() { return requestService; }
    public BuildingService getBuildingService() { return buildingService; }
    public ElevatorService getElevatorService() { return elevatorService; }
}

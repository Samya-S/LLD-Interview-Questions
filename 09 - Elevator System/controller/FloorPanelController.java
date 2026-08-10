package controller;

import domain.Direction;
import domain.ExternalRequest;
import service.BuildingService;
import service.DispatcherService;
import service.RequestService;

public class FloorPanelController {
    private final RequestService requestService;
    private final BuildingService buildingService;
    private final DispatcherService dispatcherService;
    
    public FloorPanelController(ElevatorController elevatorController) {
        this.requestService = elevatorController.getRequestService();
        this.buildingService = elevatorController.getBuildingService();
        this.dispatcherService = elevatorController.getDispatcherService();
    }
    
    public void pressUpButton(int floorNumber, String buildingId) {
        if (!buildingService.isValidFloor(buildingId, floorNumber)) {
            throw new IllegalArgumentException("Invalid floor number: " + floorNumber);
        }
        
        if (!buildingService.isSystemRunning(buildingId)) {
            System.out.println("Elevator system is not running. Request rejected.");
            return;
        }
        
        ExternalRequest request = requestService.createExternalRequest(floorNumber, Direction.UP, buildingId);
        dispatcherService.queueExternalRequest(request);
        
        System.out.println("UP button pressed on floor " + floorNumber);
    }
    
    public void pressDownButton(int floorNumber, String buildingId) {
        if (!buildingService.isValidFloor(buildingId, floorNumber)) {
            throw new IllegalArgumentException("Invalid floor number: " + floorNumber);
        }
        
        if (!buildingService.isSystemRunning(buildingId)) {
            System.out.println("Elevator system is not running. Request rejected.");
            return;
        }
        
        ExternalRequest request = requestService.createExternalRequest(floorNumber, Direction.DOWN, buildingId);
        dispatcherService.queueExternalRequest(request);
        
        System.out.println("DOWN button pressed on floor " + floorNumber);
    }
}

package service;

import domain.*;
import domain.strategy.MovementStrategy;
import domain.strategy.ScanStrategy;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MovementService {
    private final ElevatorService elevatorService;
    private final RequestService requestService;
    private final BuildingService buildingService;
    private final DispatcherService dispatcherService;
    private MovementStrategy movementStrategy;
    private ScheduledExecutorService scheduler;
    private volatile boolean systemRunning = false;
    
    public MovementService(ElevatorService elevatorService, RequestService requestService, 
                          BuildingService buildingService, DispatcherService dispatcherService) {
        this.elevatorService = elevatorService;
        this.requestService = requestService;
        this.buildingService = buildingService;
        this.dispatcherService = dispatcherService;
        this.movementStrategy = new ScanStrategy(); // Default strategy
    }
    
    public void startElevatorSystem(String buildingId) {
        systemRunning = true;
        buildingService.setBuildingSystemState(buildingId, SystemState.RUNNING);
        
        scheduler = Executors.newScheduledThreadPool(2);
        
        // Schedule request processing
        scheduler.scheduleAtFixedRate(() -> {
            if (systemRunning) {
                // these external req's have not been assigned to an elevator yet
                dispatcherService.processPendingRequests(buildingId);
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        // Schedule elevator movement processing
        scheduler.scheduleAtFixedRate(() -> {
            if (systemRunning) {
                processAllElevatorMovements(buildingId);
            }
        }, 0, 2, TimeUnit.SECONDS);
        
        System.out.println("Elevator system started for building: " + buildingId);
    }
    
    public void stopElevatorSystem(String buildingId) {
        buildingService.setBuildingSystemState(buildingId, SystemState.STOPPING);
        System.out.println("Stopping elevator system gracefully...");
        
        // Continue processing until all requests are completed
        while (hasPendingRequests(buildingId)) {
            processAllElevatorMovements(buildingId);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        systemRunning = false;
        if (scheduler != null) {
            scheduler.shutdown();
        }
        buildingService.setBuildingSystemState(buildingId, SystemState.STOPPED);
        System.out.println("Elevator system stopped gracefully");
    }
    
    public void processAllElevatorMovements(String buildingId) {
        List<Elevator> elevators = elevatorService.getAllElevators(buildingId);
        
        for (Elevator elevator : elevators) {
            if (elevator.isActive()) {
                processElevatorMovement(elevator.getId(), elevator);
            }
        }
    }
    
    public void processElevatorMovement(String elevatorId, Elevator elevator) {
        List<InternalRequest> pendingRequests = requestService.getPendingRequestsForElevator(elevatorId);
        
        if (pendingRequests.isEmpty()) {
            // No requests - check if in pre-maintenance mode
            if (elevator.getStateHandler() instanceof domain.state.PreMaintenanceState) {
                // Transition to full maintenance
                domain.state.PreMaintenanceState preMaintenanceState = (domain.state.PreMaintenanceState) elevator.getStateHandler();
                preMaintenanceState.checkForMaintenanceTransition(elevator);
                elevatorService.updateElevatorState(elevatorId, elevator.getState());
                return;
            }
            
            // Normal idle state
            if (elevator.getDirection() != Direction.IDLE) {
                elevator.setDirection(Direction.IDLE);
                elevator.setState(ElevatorState.STOPPED);
                elevatorService.updateElevatorState(elevatorId, ElevatorState.STOPPED);
            }
            return;
        }
        
        List<Integer> path = movementStrategy.calculatePath(elevator, pendingRequests);
        
        if (!path.isEmpty()) {
            // nextFloor = path.get(0)
            // Move the elevator to the next floor by changing the state to moving if not
            // move ahead and work for other elevators.. 
        }
    }

    
    private boolean hasPendingRequests(String buildingId) {
        List<Elevator> elevators = elevatorService.getAllElevators(buildingId);
        
        for (Elevator elevator : elevators) {
            List<InternalRequest> pendingRequests = requestService.getPendingRequestsForElevator(elevator.getId());
            if (!pendingRequests.isEmpty()) {
                return true;
            }
        }
        
        return dispatcherService.getQueueSize() > 0;
    }
    
    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }
}

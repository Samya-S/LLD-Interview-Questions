package service;

import domain.Elevator;
import domain.InternalRequest;
import domain.ExternalRequest;
import domain.RequestStatus;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class ElevatorSchedulerService {
    private final ElevatorService elevatorService;
    private final RequestService requestService;
    private final MovementService movementService;
    private final DispatcherService dispatcherService;
    
    // Map of buildingId -> ScheduledFuture for running schedulers
    private final Map<String, ScheduledFuture<?>> buildingSchedulers;
    private final ScheduledExecutorService executorService;
    
    // Scheduling intervals
    private static final long ELEVATOR_PROCESSING_INTERVAL = 2; // seconds

    public ElevatorSchedulerService(ElevatorService elevatorService, 
                                   RequestService requestService,
                                   MovementService movementService,
                                   DispatcherService dispatcherService) {
        this.elevatorService = elevatorService;
        this.requestService = requestService;
        this.movementService = movementService;
        this.dispatcherService = dispatcherService;
        this.buildingSchedulers = new ConcurrentHashMap<>();
        this.executorService = Executors.newScheduledThreadPool(10);
    }

    public void startBuildingScheduler(String buildingId) {
        if (buildingSchedulers.containsKey(buildingId)) {
            System.out.println("Scheduler already running for building: " + buildingId);
            return;
        }

        System.out.println("Starting elevator scheduler for building: " + buildingId);
        
        ScheduledFuture<?> schedulerFuture = executorService.scheduleAtFixedRate(
            () -> processBuildingOperations(buildingId),
            0, // Initial delay
            ELEVATOR_PROCESSING_INTERVAL,
            TimeUnit.SECONDS
        );
        
        buildingSchedulers.put(buildingId, schedulerFuture);
        System.out.println("Elevator scheduler started for building: " + buildingId);
    }

    public void stopBuildingScheduler(String buildingId) {
        ScheduledFuture<?> schedulerFuture = buildingSchedulers.remove(buildingId);
        if (schedulerFuture != null) {
            schedulerFuture.cancel(false);
            System.out.println("Elevator scheduler stopped for building: " + buildingId);
            
            // Cancel all pending requests for this building
            requestService.initiateGracefulShutdownForBuilding(buildingId);
        } else {
            System.out.println("No scheduler running for building: " + buildingId);
        }
    }

    public boolean isSchedulerRunning(String buildingId) {
        ScheduledFuture<?> future = buildingSchedulers.get(buildingId);
        return future != null && !future.isCancelled() && !future.isDone();
    }

    public void stopAllSchedulers() {
        System.out.println("Stopping all elevator schedulers...");
        buildingSchedulers.values().forEach(future -> future.cancel(false));
        buildingSchedulers.clear();
        System.out.println("All elevator schedulers stopped");
    }

    public void shutdown() {
        System.out.println("Shutting down elevator scheduler service...");
        
        // Cancel all pending requests before stopping schedulers
        requestService.initiateGracefulShutdownForAll();
        
        stopAllSchedulers();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Elevator scheduler service shutdown complete");
    }

    private void processBuildingOperations(String buildingId) {
        try {
            // Process external requests first
            processExternalRequests(buildingId);
            
            // Process elevator movements
            processElevatorMovements(buildingId);
            
        } catch (Exception e) {
            System.err.println("Error processing building operations for " + buildingId + ": " + e.getMessage());
        }
    }

    private void processExternalRequests(String buildingId) {
        List<ExternalRequest> pendingRequests = requestService.getPendingExternalRequests(buildingId);
        
        for (ExternalRequest request : pendingRequests) {
            if (request.getStatus() == RequestStatus.PENDING) {
                System.out.println("Scheduler: Processing external request: " + request.getId());
                dispatcherService.processExternalRequest(request, buildingId);
            }
        }
    }

    private void processElevatorMovements(String buildingId) {
        List<Elevator> elevators = elevatorService.getElevatorsByBuilding(buildingId);
        
        for (Elevator elevator : elevators) {
            if (!elevator.isActive()) {
                continue; // Skip inactive elevators
            }
            
            // Process internal requests for this elevator
            List<InternalRequest> pendingInternalRequests = 
                requestService.getPendingInternalRequestsForElevator(elevator.getId());
            
            if (!pendingInternalRequests.isEmpty()) {
                System.out.println("Scheduler: Processing movement for elevator: " + elevator.getId());
                movementService.processElevatorMovement(elevator.getId(), elevator);
                // Note: MovementService will complete requests when elevator reaches destination floors
                // State Pattern will determine if elevator can actually move
            }
            
            // Check if elevator preparing for maintenance can now transition
            if (elevator.isPreparingForMaintenance()) {
                elevatorService.checkMaintenanceTransition(elevator.getId());
            }
        }
    }

    // Utility methods for monitoring
    public Map<String, Boolean> getBuildingSchedulerStatus() {
        Map<String, Boolean> status = new ConcurrentHashMap<>();
        buildingSchedulers.forEach((buildingId, future) -> 
            status.put(buildingId, !future.isCancelled() && !future.isDone()));
        return status;
    }

    public int getActiveSchedulerCount() {
        return (int) buildingSchedulers.values().stream()
            .filter(future -> !future.isCancelled() && !future.isDone())
            .count();
    }
}

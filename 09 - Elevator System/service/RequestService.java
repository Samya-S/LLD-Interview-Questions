package service;

import domain.ExternalRequest;
import domain.InternalRequest;
import domain.RequestStatus;
import domain.Direction;
import repository.ExternalRequestRepository;
import repository.InternalRequestRepository;
import repository.impl.ExternalRequestRepositoryImpl;
import repository.impl.InternalRequestRepositoryImpl;
import java.util.List;

public class RequestService {
    private final ExternalRequestRepository externalRequestRepository;
    private final InternalRequestRepository internalRequestRepository;
    
    public RequestService() {
        this.externalRequestRepository = new ExternalRequestRepositoryImpl();
        this.internalRequestRepository = new InternalRequestRepositoryImpl();
    }
    
    public ExternalRequest createExternalRequest(int floor, Direction direction, String buildingId) {
        ExternalRequest request = new ExternalRequest(buildingId, floor, direction);
        return externalRequestRepository.save(request);
    }
    
    public InternalRequest createInternalRequest(String elevatorId, int destinationFloor) {
        InternalRequest request = new InternalRequest(elevatorId, destinationFloor);
        return internalRequestRepository.save(request);
    }
    
    public void completeExternalRequest(String requestId) {
        externalRequestRepository.updateRequestStatus(requestId, RequestStatus.COMPLETED);
    }
    
    public void completeInternalRequest(String requestId) {
        internalRequestRepository.updateRequestStatus(requestId, RequestStatus.COMPLETED);
    }
    
    public List<ExternalRequest> getQueuedRequests(String buildingId) {
        return externalRequestRepository.findQueuedRequests(buildingId);
    }
    
    public List<InternalRequest> getPendingRequestsForElevator(String elevatorId) {
        return internalRequestRepository.findPendingByElevator(elevatorId);
    }
    
    public void assignRequestToElevator(String requestId, String elevatorId) {
        externalRequestRepository.findById(requestId).ifPresent(request -> {
            request.setAssignedElevatorId(elevatorId);
            request.setStatus(RequestStatus.ASSIGNED);
            externalRequestRepository.save(request);
            
            // CRITICAL: Create corresponding internal request so elevator actually moves to the floor
            InternalRequest internalRequest = new InternalRequest(elevatorId, request.getFloorNumber());
            internalRequestRepository.save(internalRequest);
            
            System.out.println("Created internal request for elevator " + elevatorId + " to serve external request at floor " + request.getFloorNumber());
        });
    }
    
    public List<ExternalRequest> getAssignedRequestsForElevator(String elevatorId) {
        return externalRequestRepository.findAll().stream()
                .filter(req -> elevatorId.equals(req.getAssignedElevatorId()))
                .filter(req -> req.getStatus() == RequestStatus.ASSIGNED)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public void initiateGracefulShutdownForBuilding(String buildingId) {
        // For graceful degradation, we don't cancel requests
        // Instead, we let them complete naturally through pre-maintenance mode
        System.out.println("Graceful shutdown initiated for building " + buildingId + " - allowing pending requests to complete");
    }
    
    public void initiateGracefulShutdownForAll() {
        // For graceful degradation, we don't cancel requests
        // Instead, we let them complete naturally through pre-maintenance mode
        System.out.println("Graceful shutdown initiated for all elevators - allowing pending requests to complete");
    }
    
    public List<ExternalRequest> getPendingExternalRequests(String buildingId) {
        return externalRequestRepository.findAll().stream()
                .filter(req -> buildingId.equals(req.getBuildingId()))
                .filter(req -> req.getStatus() == RequestStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public List<InternalRequest> getPendingInternalRequestsForElevator(String elevatorId) {
        return internalRequestRepository.findAll().stream()
                .filter(req -> elevatorId.equals(req.getElevatorId()))
                .filter(req -> req.getStatus() == RequestStatus.PENDING)
                .collect(java.util.stream.Collectors.toList());
    }
}

package domain;

import java.util.UUID;

public class ExternalRequest {
    private String id;
    private String buildingId;
    private int floorNumber;
    private Direction direction;
    private long timestamp;
    private RequestStatus status;
    private String assignedElevatorId;
    
    public ExternalRequest(String buildingId, int floorNumber, Direction direction) {
        this.id = UUID.randomUUID().toString();
        this.buildingId = buildingId;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.timestamp = System.currentTimeMillis();
        this.status = RequestStatus.QUEUED;
    }
    
    // Getters
    public String getId() { return id; }
    public String getBuildingId() { return buildingId; }
    public int getFloorNumber() { return floorNumber; }
    public Direction getDirection() { return direction; }
    public long getTimestamp() { return timestamp; }
    public RequestStatus getStatus() { return status; }
    public String getAssignedElevatorId() { return assignedElevatorId; }
    
    // Setters
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public void setAssignedElevatorId(String assignedElevatorId) {
        this.assignedElevatorId = assignedElevatorId;
    }
}

package domain;

import java.util.UUID;

public class InternalRequest {
    private String id;
    private String elevatorId;
    private int destinationFloor;
    private long timestamp;
    private RequestStatus status;
    
    public InternalRequest(String elevatorId, int destinationFloor) {
        this.id = UUID.randomUUID().toString();
        this.elevatorId = elevatorId;
        this.destinationFloor = destinationFloor;
        this.timestamp = System.currentTimeMillis();
        this.status = RequestStatus.PENDING;
    }
    
    // Getters
    public String getId() { return id; }
    public String getElevatorId() { return elevatorId; }
    public int getDestinationFloor() { return destinationFloor; }
    public long getTimestamp() { return timestamp; }
    public RequestStatus getStatus() { return status; }
    
    // Setters
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}

package domain;

import domain.state.ElevatorStateHandler;
import domain.state.StoppedState;
import java.util.UUID;

public class Elevator {
    private String id;
    private String buildingId;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private int capacity;
    private int currentLoad;
    private boolean isActive;
    private ElevatorStateHandler stateHandler;
    
    public Elevator(String buildingId, int capacity) {
        this.id = UUID.randomUUID().toString();
        this.buildingId = buildingId;
        this.currentFloor = 1; // Start at ground floor
        this.direction = Direction.IDLE;
        this.state = ElevatorState.STOPPED;
        this.capacity = capacity;
        this.currentLoad = 0;
        this.isActive = true;
        this.stateHandler = new StoppedState(); // Initialize with stopped state
    }
    
    // Getters
    public String getId() { return id; }
    public String getBuildingId() { return buildingId; }
    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
    public ElevatorState getState() { return state; }
    public int getCapacity() { return capacity; }
    public int getCurrentLoad() { return currentLoad; }
    public boolean isActive() { return isActive; }
    public ElevatorStateHandler getStateHandler() { return stateHandler; }
    
    // Setters
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public void setState(ElevatorState state) {
        this.state = state;
    }
    
    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public void setStateHandler(ElevatorStateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }
    
    public boolean isAvailable() {
        return stateHandler.canAcceptExternalRequests(this);
    }
    
    public boolean canAcceptInternalRequests() {
        return stateHandler.canAcceptInternalRequests(this);
    }
    
    public boolean isFull() {
        return currentLoad >= capacity;
    }
    
    // State pattern delegation methods
    
    public void openDoors() {
        stateHandler.openDoors(this);
    }
    
    public void closeDoors() {
        stateHandler.closeDoors(this);
    }
    
    public void enterMaintenance() {
        stateHandler.enterMaintenance(this);
    }
    
    public void exitMaintenance() {
        stateHandler.exitMaintenance(this);
    }
    
    public boolean isPreparingForMaintenance() {
        return stateHandler instanceof domain.state.PreMaintenanceState;
    }
}

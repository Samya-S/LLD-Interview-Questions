package domain;

import java.util.UUID;

public class Building {
    private String id;
    private String name;
    private int minFloor;
    private int maxFloor;
    private int totalElevators;
    private SystemState systemState;
    
    public Building(String name, int minFloor, int maxFloor, int totalElevators) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.totalElevators = totalElevators;
        this.systemState = SystemState.STOPPED;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public int getMinFloor() { return minFloor; }
    public int getMaxFloor() { return maxFloor; }
    public int getTotalElevators() { return totalElevators; }
    public SystemState getSystemState() { return systemState; }
    
    public void setSystemState(SystemState systemState) {
        this.systemState = systemState;
    }
    
    public boolean isValidFloor(int floor) {
        return floor >= minFloor && floor <= maxFloor;
    }
}

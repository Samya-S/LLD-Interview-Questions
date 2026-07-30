package domain;

import java.util.UUID;

public class ParkingSlot {
    private UUID id;
    private Vehicle.VehicleType slotType;
    private boolean isOccupied;
    private int floorNumber;

    public ParkingSlot(Vehicle.VehicleType slotType, int floorNumber) {
        this.id = UUID.randomUUID();
        this.slotType = slotType;
        this.isOccupied = false;
        this.floorNumber = floorNumber;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public Vehicle.VehicleType getSlotType() {
        return slotType;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "id=" + id +
                ", slotType=" + slotType +
                ", isOccupied=" + isOccupied +
                ", floorNumber=" + floorNumber +
                '}';
    }
} 
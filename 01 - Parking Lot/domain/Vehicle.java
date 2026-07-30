package domain;

import java.util.UUID;

public class Vehicle {
    private UUID id;
    private String licensePlate;
    private VehicleType vehicleType;

    public enum VehicleType {
        BIKE, CAR, TRUCK, EV
    }

    public Vehicle(String licensePlate, VehicleType vehicleType) {
        this.id = UUID.randomUUID();
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", licensePlate='" + licensePlate + '\'' +
                ", vehicleType=" + vehicleType +
                '}';
    }
} 
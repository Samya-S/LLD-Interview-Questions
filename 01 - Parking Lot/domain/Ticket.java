package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {
    private UUID id;
    private UUID vehicleId;
    private UUID slotId;
    private LocalDateTime entryTime;
    private boolean isActive;

    public Ticket(UUID vehicleId, UUID slotId) {
        this.id = UUID.randomUUID();
        this.vehicleId = vehicleId;
        this.slotId = slotId;
        this.entryTime = LocalDateTime.now();
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public UUID getSlotId() {
        return slotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", vehicleId=" + vehicleId +
                ", slotId=" + slotId +
                ", entryTime=" + entryTime +
                ", isActive=" + isActive +
                '}';
    }
} 
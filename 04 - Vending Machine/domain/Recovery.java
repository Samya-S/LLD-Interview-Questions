package domain;

import domain.state.VendingMachineState;

public class Recovery {
    private int id;
    private int vendingMachineId;
    private int transactionId;
    private VendingMachineState state;
    private RecoveryStatus status;
    private long createdAt;
    private Long completedAt;
    
    public Recovery(int id, int vendingMachineId, int transactionId, VendingMachineState state) {
        this.id = id;
        this.vendingMachineId = vendingMachineId;
        this.transactionId = transactionId;
        this.state = state;
        this.status = RecoveryStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
        this.completedAt = null;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public int getVendingMachineId() { return vendingMachineId; }
    public int getTransactionId() { return transactionId; }
    public VendingMachineState getState() { return state; }
    public RecoveryStatus getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }
    public Long getCompletedAt() { return completedAt; }
    
    public void setId(int id) { this.id = id; }
    public void setStatus(RecoveryStatus status) { this.status = status; }
    public void setCompletedAt(Long completedAt) { this.completedAt = completedAt; }
    
    public void markComplete() {
        this.status = RecoveryStatus.COMPLETED;
        this.completedAt = System.currentTimeMillis();
    }
    
    public boolean isPending() {
        return status == RecoveryStatus.PENDING;
    }
    
    public boolean isCompleted() {
        return status == RecoveryStatus.COMPLETED;
    }
    
    @Override
    public String toString() {
        return "Recovery " + id + " - Machine: " + vendingMachineId + 
               ", Transaction: " + transactionId + ", Status: " + status;
    }
}

package domain;

public class Transaction {
    private int id;
    private int vendingMachineId;
    private int productId;
    private double amountInserted;
    private double amountRequired;
    private double changeReturned;
    private TransactionStatus status;
    private long timestamp;
    
    public Transaction(int id, int vendingMachineId, int productId, double amountRequired) {
        this.id = id;
        this.vendingMachineId = vendingMachineId;
        this.productId = productId;
        this.amountRequired = amountRequired;
        this.amountInserted = 0.0;
        this.changeReturned = 0.0;
        this.status = TransactionStatus.PENDING;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public int getVendingMachineId() { return vendingMachineId; }
    public int getProductId() { return productId; }
    public double getAmountInserted() { return amountInserted; }
    public double getAmountRequired() { return amountRequired; }
    public double getChangeReturned() { return changeReturned; }
    public TransactionStatus getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
    
    public void setId(int id) { this.id = id; }
    public void setAmountInserted(double amountInserted) { this.amountInserted = amountInserted; }
    public void setChangeReturned(double changeReturned) { this.changeReturned = changeReturned; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public boolean isPaymentComplete() {
        return amountInserted >= amountRequired;
    }
    
    public double getRemainingAmount() {
        return Math.max(0, amountRequired - amountInserted);
    }
    
    public void addPayment(double amount) {
        this.amountInserted += amount;
        if (isPaymentComplete()) {
            this.status = TransactionStatus.COMPLETED;
        }
    }
    
    public void cancel() {
        this.status = TransactionStatus.CANCELLED;
    }
    
    public void fail(String reason) {
        this.status = TransactionStatus.FAILED;
    }
    
    @Override
    public String toString() {
        return "Transaction " + id + " - Product: " + productId + 
               ", Status: " + status + ", Amount: $" + amountRequired;
    }
}

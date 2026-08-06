package domain;

public class Inventory {
    private int productId;
    private int vendingMachineId;
    private int quantity;
    private int minThreshold;
    
    public Inventory(int productId, int vendingMachineId, int quantity, int minThreshold) {
        this.productId = productId;
        this.vendingMachineId = vendingMachineId;
        this.quantity = quantity;
        this.minThreshold = minThreshold;
    }
    
    // Getters
    public int getProductId() { return productId; }
    public int getVendingMachineId() { return vendingMachineId; }
    public int getQuantity() { return quantity; }
    public int getMinThreshold() { return minThreshold; }
    
    // Setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setVendingMachineId(int vendingMachineId) { this.vendingMachineId = vendingMachineId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setMinThreshold(int minThreshold) { this.minThreshold = minThreshold; }
    
    public boolean isLowStock() {
        return quantity <= minThreshold;
    }
    
    public boolean isOutOfStock() {
        return quantity <= 0;
    }
    
    public void addQuantity(int amount) {
        this.quantity += amount;
    }
    
    public void removeQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            this.quantity = 0;
        }
    }
    
    @Override
    public String toString() {
        return "Product " + productId + " - Quantity: " + quantity + " (Min: " + minThreshold + ")";
    }
}

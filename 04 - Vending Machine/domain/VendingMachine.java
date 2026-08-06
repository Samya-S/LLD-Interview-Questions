package domain;

import domain.state.VendingMachineState;
import domain.state.IdleState;
import java.util.Map;
import java.util.HashMap;

public class VendingMachine {
    private int id;
    private String location;
    private VendingMachineState currentState;
    private Transaction currentTransaction;
    private Map<Product, Integer> inventory;
    private CashBox cashBox;
    private boolean isOperational;
    
    public VendingMachine(int id, String location) {
        this.id = id;
        this.location = location;
        this.currentState = new IdleState();
        this.inventory = new HashMap<>();
        this.cashBox = new CashBox(1, id);
        this.isOperational = true;
        System.out.println("VendingMachine created: " + location + " (ID: " + id + ")");
    }
    
    public void setState(VendingMachineState newState) {
        this.currentState = newState;
        System.out.println("Machine " + id + " state changed to: " + newState.getStateName());
    }
    
    public Transaction processPayment(PaymentRequest request) {
        return currentState.processPayment(this, request);
    }
    
    public void cancelPayment(int transactionId) {
        currentState.cancelPayment(this, transactionId);
    }
    
    public String getCurrentStateName() {
        return currentState.getStateName();
    }
    
    public void addProduct(Product product, int quantity) {
        inventory.put(product, inventory.getOrDefault(product, 0) + quantity);
        System.out.println("Added " + quantity + " units of " + product.getName() + " to machine " + id);
    }
    
    public boolean hasProduct(Product product) {
        return inventory.containsKey(product) && inventory.get(product) > 0;
    }
    
    public void dispenseProduct(Product product) {
        if (hasProduct(product)) {
            int currentQuantity = inventory.get(product);
            inventory.put(product, currentQuantity - 1);
            System.out.println("Dispensed " + product.getName() + " from machine " + id);
        } else {
            System.out.println("Cannot dispense " + product.getName() + " - out of stock in machine " + id);
        }
    }
    
    public void addCash(Denomination denomination, int count) {
        cashBox.addDenomination(denomination, count);
        System.out.println("Added " + count + " " + denomination + " notes to machine " + id);
    }
    
    public boolean removeCash(Denomination denomination, int count) {
        boolean success = cashBox.removeDenomination(denomination, count);
        if (success) {
            System.out.println("Removed " + count + " " + denomination + " notes from machine " + id);
        } else {
            System.out.println("Failed to remove " + count + " " + denomination + " notes from machine " + id);
        }
        return success;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public String getLocation() { return location; }
    public VendingMachineState getCurrentState() { return currentState; }
    public Transaction getCurrentTransaction() { return currentTransaction; }
    public Map<Product, Integer> getInventory() { return inventory; }
    public CashBox getCashBox() { return cashBox; }
    public boolean isOperational() { return isOperational; }
    
    public void setId(int id) { this.id = id; }
    public void setCurrentTransaction(Transaction transaction) { this.currentTransaction = transaction; }
    public void setOperational(boolean operational) { this.isOperational = operational; }
    
    @Override
    public String toString() {
        return "VendingMachine{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", state=" + currentState.getStateName() +
                ", operational=" + isOperational +
                '}';
    }
}

package domain;

import java.util.HashMap;
import java.util.Map;

public class CashBox {
    private int id;
    private int vendingMachineId;
    private Map<Denomination, Integer> denominations;
    private double totalAmount;
    
    public CashBox(int id, int vendingMachineId) {
        this.id = id;
        this.vendingMachineId = vendingMachineId;
        this.denominations = new HashMap<>();
        this.totalAmount = 0.0;
        initializeCashBox();
    }
    
    private void initializeCashBox() {
        // Initialize with some cash for change
        denominations.put(Denomination.ONE_DOLLAR, 10);
        denominations.put(Denomination.FIVE_DOLLAR, 5);
        denominations.put(Denomination.TEN_DOLLAR, 3);
        denominations.put(Denomination.TWENTY_DOLLAR, 2);
        denominations.put(Denomination.FIFTY_DOLLAR, 1);
        denominations.put(Denomination.HUNDRED_DOLLAR, 1);
        calculateTotalAmount();
    }
    
    // Getters
    public int getId() { return id; }
    public int getVendingMachineId() { return vendingMachineId; }
    public Map<Denomination, Integer> getDenominations() { return denominations; }
    public double getTotalAmount() { return totalAmount; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setVendingMachineId(int vendingMachineId) { this.vendingMachineId = vendingMachineId; }
    
    public void addDenomination(Denomination denomination, int count) {
        int currentCount = denominations.getOrDefault(denomination, 0);
        denominations.put(denomination, currentCount + count);
        calculateTotalAmount();
    }
    
    public boolean removeDenomination(Denomination denomination, int count) {
        int currentCount = denominations.getOrDefault(denomination, 0);
        if (currentCount >= count) {
            denominations.put(denomination, currentCount - count);
            calculateTotalAmount();
            return true;
        }
        return false;
    }
    
    private void calculateTotalAmount() {
        totalAmount = 0.0;
        for (Map.Entry<Denomination, Integer> entry : denominations.entrySet()) {
            totalAmount += entry.getKey().getValueInDollars() * entry.getValue();
        }
    }
    
    public boolean hasSufficientChange(double amount) {
        return totalAmount >= amount;
    }
    
    public Map<Denomination, Integer> calculateChange(double amount) {
        Map<Denomination, Integer> change = new HashMap<>();
        double remainingAmount = amount;
        
        // Start with highest denominations
        Denomination[] sortedDenominations = {
            Denomination.HUNDRED_DOLLAR,
            Denomination.FIFTY_DOLLAR,
            Denomination.TWENTY_DOLLAR,
            Denomination.TEN_DOLLAR,
            Denomination.FIVE_DOLLAR,
            Denomination.ONE_DOLLAR
        };
        
        for (Denomination denom : sortedDenominations) {
            int availableCount = denominations.getOrDefault(denom, 0);
            double denomValue = denom.getValueInDollars();
            
            if (remainingAmount >= denomValue && availableCount > 0) {
                int countNeeded = (int) (remainingAmount / denomValue);
                int countToUse = Math.min(countNeeded, availableCount);
                
                change.put(denom, countToUse);
                remainingAmount -= countToUse * denomValue;
                
                if (remainingAmount < 0.01) { // Handle floating point precision
                    break;
                }
            }
        }
        
        return change;
    }
    
    @Override
    public String toString() {
        return "CashBox " + id + " - Total: $" + totalAmount;
    }
}

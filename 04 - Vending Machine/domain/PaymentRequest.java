package domain;

import java.util.Map;

public class PaymentRequest {
    private int productId;
    private int quantity;
    private Map<Denomination, Integer> denominations;
    
    public PaymentRequest(int productId, int quantity, Map<Denomination, Integer> denominations) {
        this.productId = productId;
        this.quantity = quantity;
        this.denominations = denominations;
    }
    
    // Getters
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public Map<Denomination, Integer> getDenominations() { return denominations; }
    
    // Setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setDenominations(Map<Denomination, Integer> denominations) { this.denominations = denominations; }
    
    public double getTotalAmount() {
        double total = 0.0;
        for (Map.Entry<Denomination, Integer> entry : denominations.entrySet()) {
            total += entry.getKey().getValueInDollars() * entry.getValue();
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "PaymentRequest - Product: " + productId + ", Quantity: " + quantity + 
               ", Amount: $" + getTotalAmount();
    }
}

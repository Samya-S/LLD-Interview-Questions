package domain.state;

import domain.VendingMachine;
import domain.PaymentRequest;
import domain.Transaction;
import domain.Product;
import domain.state.ProcessingPaymentState;
import java.util.Map;

public class IdleState implements VendingMachineState {
    
    @Override
    public Transaction processPayment(VendingMachine machine, PaymentRequest request) {
        System.out.println("IdleState: Processing payment request for product " + request.getProductId());
        
        // TODO: Validate product exists and has sufficient stock
        // TODO: Check if machine has sufficient change
        
        // Create transaction
        Transaction transaction = new Transaction(0, machine.getId(), request.getProductId(), request.getTotalAmount());
        machine.setCurrentTransaction(transaction);
        
        // Process the payment
        double amountInserted = request.getTotalAmount();
        transaction.addPayment(amountInserted);
        
        // Add cash to machine
        for (Map.Entry<domain.Denomination, Integer> entry : request.getDenominations().entrySet()) {
            machine.addCash(entry.getKey(), entry.getValue());
        }
        
        // Change to processing payment state
        machine.setState(new ProcessingPaymentState());
        
        // Simulate payment processing and then transition to dispensing state
        try {
            Thread.sleep(1000); // Simulate payment processing time
            machine.setState(new domain.state.DispensingState());
            
            // Dispense the product
            Product product = machine.getInventory().keySet().stream()
                .filter(p -> p.getId() == request.getProductId())
                .findFirst()
                .orElse(null);
            
            if (product != null) {
                machine.dispenseProduct(product);
                // Change state back to IdleState after dispensing
                machine.setState(new IdleState());
            }
            
        } catch (InterruptedException e) {
            System.out.println("IdleState: Payment processing interrupted");
            machine.setState(new IdleState());
        }
        
        System.out.println("IdleState: Payment processed, transaction created: " + transaction.getId());
        return transaction;
    }
    
    @Override
    public void cancelPayment(VendingMachine machine, int transactionId) {
        System.out.println("IdleState: No active transaction to cancel");
        // No action needed in idle state
    }
    
    @Override
    public String getStateName() {
        return "IDLE";
    }
}

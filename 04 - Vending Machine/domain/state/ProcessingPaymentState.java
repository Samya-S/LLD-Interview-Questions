package domain.state;

import domain.VendingMachine;
import domain.PaymentRequest;
import domain.Transaction;
import domain.state.IdleState;
import domain.state.DispensingState;

public class ProcessingPaymentState implements VendingMachineState {
    
    @Override
    public Transaction processPayment(VendingMachine machine, PaymentRequest request) {
        System.out.println("ProcessingPaymentState: Cannot process new payment while already processing");
        // Cannot process new payment while already processing
        return null;
    }
    
    @Override
    public void cancelPayment(VendingMachine machine, int transactionId) {
        System.out.println("ProcessingPaymentState: Cancelling payment for transaction " + transactionId);
        
        Transaction transaction = machine.getCurrentTransaction();
        if (transaction != null && transaction.getId() == transactionId) {
            // Refund the payment
            double amountToRefund = transaction.getAmountInserted();
            // TODO: Implement actual refund logic
            
            // Cancel the transaction
            transaction.cancel();
            
            // Clear current transaction
            machine.setCurrentTransaction(null);
            
            // Return to idle state
            machine.setState(new IdleState());
            
            System.out.println("ProcessingPaymentState: Payment cancelled, refunded $" + amountToRefund);
        } else {
            System.out.println("ProcessingPaymentState: Transaction not found for cancellation");
        }
    }
    
    @Override
    public String getStateName() {
        return "PROCESSING_PAYMENT";
    }
}

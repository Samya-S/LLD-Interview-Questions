package domain.state;

import domain.VendingMachine;
import domain.PaymentRequest;
import domain.Transaction;
import domain.state.IdleState;

public class DispensingState implements VendingMachineState {
    
    @Override
    public Transaction processPayment(VendingMachine machine, PaymentRequest request) {
        System.out.println("DispensingState: Cannot process new payment while dispensing");
        // Cannot process new payment while dispensing
        return null;
    }
    
    @Override
    public void cancelPayment(VendingMachine machine, int transactionId) {
        System.out.println("DispensingState: Cannot cancel payment while dispensing");
        // Cannot cancel payment while dispensing
    }
    
    @Override
    public String getStateName() {
        return "DISPENSING";
    }
}

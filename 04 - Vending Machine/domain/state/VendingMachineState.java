package domain.state;

import domain.VendingMachine;
import domain.PaymentRequest;
import domain.Transaction;

public interface VendingMachineState {
    Transaction processPayment(VendingMachine machine, PaymentRequest request);
    void cancelPayment(VendingMachine machine, int transactionId);
    String getStateName();
}

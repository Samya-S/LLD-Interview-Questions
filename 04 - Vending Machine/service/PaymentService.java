package service;

import domain.VendingMachine;
import domain.Transaction;
import domain.PaymentRequest;
import domain.Denomination;
import repository.VendingMachineRepository;
import repository.PaymentRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class PaymentService {
    private VendingMachineRepository vendingMachineRepository;
    private PaymentRepository paymentRepository;
    
    public PaymentService(VendingMachineRepository vendingMachineRepository, PaymentRepository paymentRepository) {
        this.vendingMachineRepository = vendingMachineRepository;
        this.paymentRepository = paymentRepository;
        System.out.println("PaymentService initialized");
    }
    
    public Transaction processPayment(int machineId, PaymentRequest request) {
        System.out.println("PaymentService: Processing payment for machine " + machineId + ", product " + request.getProductId());
        
        // TODO: Validate machine ID
        VendingMachine machine = vendingMachineRepository.findById(machineId);
        if (machine == null) {
            System.out.println("PaymentService: Machine not found");
            return null;
        }
        
        
        // Process payment through the machine's state
        Transaction transaction = machine.processPayment(request);
        
        if (transaction != null) {
            // Save transaction to repository
            paymentRepository.saveTransaction(transaction);
            System.out.println("PaymentService: Payment processed successfully, transaction ID: " + transaction.getId());
        } else {
            System.out.println("PaymentService: Payment processing failed");
        }
        
        return transaction;
    }
    
    public void cancelPayment(int machineId, int transactionId) {
        System.out.println("PaymentService: Cancelling payment for machine " + machineId + ", transaction " + transactionId);
        
        // TODO: Validate machine ID and transaction ID
        VendingMachine machine = vendingMachineRepository.findById(machineId);
        if (machine != null) {
            machine.cancelPayment(transactionId);
            
            // Update transaction status in repository
            Transaction transaction = paymentRepository.findById(transactionId);
            if (transaction != null) {
                transaction.cancel();
                paymentRepository.saveTransaction(transaction);
            }
            
            System.out.println("PaymentService: Payment cancelled successfully");
        } else {
            System.out.println("PaymentService: Machine not found for payment cancellation");
        }
    }
    
    public String getPaymentStatus(int machineId, int transactionId) {
        System.out.println("PaymentService: Getting payment status for machine " + machineId + ", transaction " + transactionId);
        
        // TODO: Implement actual payment status check
        Transaction transaction = paymentRepository.findById(transactionId);
        if (transaction != null) {
            return transaction.getStatus().toString();
        }
        return "NOT_FOUND";
    }
    
    public void updateCashBox(int machineId, Map<Denomination, Integer> denominations) {
        System.out.println("PaymentService: Updating cash box for machine " + machineId);
        
        // TODO: Implement actual cash box update logic
        paymentRepository.updateCashBox(machineId, denominations);
        
        System.out.println("PaymentService: Cash box updated successfully");
    }
    
    public List<Transaction> getTransactionHistory(int machineId) {
        System.out.println("PaymentService: Getting transaction history for machine " + machineId);
        
        // TODO: Implement actual transaction history retrieval
        List<Transaction> transactions = paymentRepository.findByMachine(machineId);
        
        System.out.println("PaymentService: Retrieved " + transactions.size() + " transactions");
        return transactions;
    }
    
    public double getTotalCashInMachine(int machineId) {
        System.out.println("PaymentService: Getting total cash in machine " + machineId);
        
        double totalCash = paymentRepository.getTotalCashInMachine(machineId);
        System.out.println("PaymentService: Total cash in machine: $" + totalCash);
        
        return totalCash;
    }
    
    public Map<Denomination, Integer> getCashBoxStatus(int machineId) {
        System.out.println("PaymentService: Getting cash box status for machine " + machineId);
        
        Map<Denomination, Integer> cashBox = paymentRepository.getCashBox(machineId);
        System.out.println("PaymentService: Cash box status retrieved");
        
        return cashBox;
    }
}

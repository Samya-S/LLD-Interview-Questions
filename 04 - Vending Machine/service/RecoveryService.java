package service;

import domain.VendingMachine;
import domain.Recovery;
import domain.RecoveryStatus;
import domain.Transaction;
import domain.state.VendingMachineState;
import domain.state.IdleState;
import repository.VendingMachineRepository;
import repository.RecoveryRepository;
import repository.PaymentRepository;
import java.util.List;
import java.util.ArrayList;

public class RecoveryService {
    private VendingMachineRepository vendingMachineRepository;
    private RecoveryRepository recoveryRepository;
    private PaymentRepository paymentRepository;
    
    public RecoveryService(VendingMachineRepository vendingMachineRepository, 
                          RecoveryRepository recoveryRepository,
                          PaymentRepository paymentRepository) {
        this.vendingMachineRepository = vendingMachineRepository;
        this.recoveryRepository = recoveryRepository;
        this.paymentRepository = paymentRepository;
        System.out.println("RecoveryService initialized");
    }
    
    public void performRecovery(int machineId) {
        VendingMachine machine = vendingMachineRepository.findById(machineId);
        if(machine == null) {
            System.out.println("Invalid machineid");
            return;
        }
        System.out.println("RecoveryService: Performing recovery for machine " + machineId);
        
        // Step 1: Get and process pending recoveries for the machine
        List<Recovery> pendingRecoveries = recoveryRepository.findPendingRecoveries(machineId);
        
        if (!pendingRecoveries.isEmpty()) {
            System.out.println("RecoveryService: Found " + pendingRecoveries.size() + " pending recoveries");
            for (Recovery recovery : pendingRecoveries) {
                processRecovery(recovery);
            }
            machine.setState(new IdleState());
            System.out.println("RecoveryService: Machine " + machineId + " reset to IDLE state");
            return;
        } else {
            System.out.println("RecoveryService: No pending recoveries for machine " + machineId);
        }
        
        // Step 2: Check machine state and create recoveries if needed
        if (machine != null) {
            String currentState = machine.getCurrentStateName();
            if (!"IDLE".equals(currentState)) {
                System.out.println("RecoveryService: Machine " + machineId + " found in " + currentState + " state, creating recovery");
                
                // Find incomplete transactions for this machine
                List<Transaction> incompleteTransactions = findIncompleteTransactions(machineId);
                
                if (!incompleteTransactions.isEmpty()) {
                    // Create recovery for the most recent incomplete transaction
                    Transaction latestTransaction = incompleteTransactions.get(incompleteTransactions.size() - 1);
                    createRecoveryEntry(machineId, latestTransaction.getId(), machine.getCurrentState());
                    
                    // Process the newly created recovery
                    List<Recovery> newRecoveries = recoveryRepository.findPendingRecoveries(machineId);
                    for (Recovery recovery : newRecoveries) {
                        if (recovery.getTransactionId() == latestTransaction.getId()) {
                            performRecovery(machineId);
                        }
                    }
                } else {
                    // No incomplete transactions, just reset state
                    System.out.println("RecoveryService: No incomplete transactions found, resetting machine state to IDLE");
                }
                
                // Reset to safe state
                machine.setState(new IdleState());
                System.out.println("RecoveryService: Machine " + machineId + " reset to IDLE state");
            }
        }
        
        System.out.println("RecoveryService: Recovery completed for machine " + machineId);
    }
    
    private void processRecovery(Recovery recovery) {
        System.out.println("RecoveryService: Processing recovery " + recovery.getId());
        
        int machineId = recovery.getVendingMachineId();
        int transactionId = recovery.getTransactionId();
        VendingMachineState state = recovery.getState();
        
        // Get the transaction
        Transaction transaction = paymentRepository.findById(transactionId);
        if (transaction == null) {
            System.out.println("RecoveryService: Transaction not found for recovery " + recovery.getId());
            recovery.markComplete();
            recoveryRepository.saveRecovery(recovery);
            return;
        }
        
        // Process recovery based on the state
        String stateName = state.getStateName();
        if ("PROCESSING_PAYMENT".equals(stateName)) {
            // Refund the payment
            handleProcessingPaymentRecovery(recovery, transaction);
        } else if ("DISPENSING".equals(stateName)) {
            // Complete the dispensing
            handleDispensingRecovery(recovery, transaction);
        } else {
            // Unknown state, mark as completed
            System.out.println("RecoveryService: Unknown state for recovery " + recovery.getId() + ": " + stateName);
            recovery.markComplete();
        }
        
        // Mark recovery as completed
        recoveryRepository.saveRecovery(recovery);
        System.out.println("RecoveryService: Recovery " + recovery.getId() + " processed successfully");
    }
    
    private void handleProcessingPaymentRecovery(Recovery recovery, Transaction transaction) {
        System.out.println("RecoveryService: Handling PROCESSING_PAYMENT recovery for transaction " + transaction.getId());
        
        // TODO: Implement actual refund logic
        // For now, just cancel the transaction
        transaction.cancel();
        paymentRepository.saveTransaction(transaction);
        
        System.out.println("RecoveryService: Refunded payment for transaction " + transaction.getId());
    }
    
    private void handleDispensingRecovery(Recovery recovery, Transaction transaction) {
        System.out.println("RecoveryService: Handling DISPENSING recovery for transaction " + transaction.getId());
        
        // TODO: Implement actual dispensing logic
        // For now, just mark the transaction as completed
        transaction.setStatus(domain.TransactionStatus.COMPLETED);
        paymentRepository.saveTransaction(transaction);
        
        System.out.println("RecoveryService: Completed dispensing for transaction " + transaction.getId());
    }
    
    public RecoveryStatus getRecoveryStatus(int machineId) {
        System.out.println("RecoveryService: Getting recovery status for machine " + machineId);
        
        // TODO: Implement actual recovery status check
        List<Recovery> pendingRecoveries = recoveryRepository.findPendingRecoveries(machineId);
        
        if (pendingRecoveries.isEmpty()) {
            return RecoveryStatus.COMPLETED;
        } else {
            return RecoveryStatus.PENDING;
        }
    }
    
    public void createRecoveryEntry(int machineId, int transactionId, VendingMachineState state) {
        System.out.println("RecoveryService: Creating recovery entry for machine " + machineId + 
                          ", transaction " + transactionId + ", state " + state.getStateName());
        
        Recovery recovery = new Recovery(0, machineId, transactionId, state);
        recoveryRepository.saveRecovery(recovery);
        
        System.out.println("RecoveryService: Recovery entry created with ID " + recovery.getId());
    }
    
    public void markRecoveryComplete(int machineId, int recoveryId) {
        System.out.println("RecoveryService: Marking recovery " + recoveryId + " as complete for machine " + machineId);
        
        // TODO: Implement actual completion marking
        recoveryRepository.markComplete(recoveryId);
        
        System.out.println("RecoveryService: Recovery " + recoveryId + " marked as complete");
    }
    
    public List<Recovery> getPendingRecoveries(int machineId) {
        System.out.println("RecoveryService: Getting pending recoveries for machine " + machineId);
        return recoveryRepository.findPendingRecoveries(machineId);
    }
    
    /**
     * Check and recover during system startup
     */
    public void checkAndRecover() {
        System.out.println("RecoveryService: Checking and recovering during system startup");
        
        // Get all machines and check their states
        List<VendingMachine> machines = vendingMachineRepository.findAll();
        
        for (VendingMachine machine : machines) {
            String currentState = machine.getCurrentStateName();
            if (!"IDLE".equals(currentState)) {
                System.out.println("RecoveryService: Machine " + machine.getId() + " found in " + currentState + " state during startup");
                
                // Find incomplete transactions for this machine
                List<Transaction> incompleteTransactions = findIncompleteTransactions(machine.getId());
                
                if (!incompleteTransactions.isEmpty()) {
                    // Create recovery for the most recent incomplete transaction
                    Transaction latestTransaction = incompleteTransactions.get(incompleteTransactions.size() - 1);
                    createRecoveryEntry(machine.getId(), latestTransaction.getId(), machine.getCurrentState());
                    
                    // Process the newly created recovery
                    List<Recovery> newRecoveries = recoveryRepository.findPendingRecoveries(machine.getId());
                    for (Recovery recovery : newRecoveries) {
                        if (recovery.getTransactionId() == latestTransaction.getId()) {
                            processRecovery(recovery);
                        }
                    }
                } else {
                    // No incomplete transactions, just create recovery with transaction ID 0
                    createRecoveryEntry(machine.getId(), 0, machine.getCurrentState());
                }
                
                // Reset to safe state
                machine.setState(new IdleState());
                System.out.println("RecoveryService: Machine " + machine.getId() + " reset to IDLE state");
            }
        }
        
        System.out.println("RecoveryService: System startup recovery completed");
    }

    private List<Transaction> findIncompleteTransactions(int machineId) {
        List<Transaction> allTransactions = paymentRepository.findByMachine(machineId);
        List<Transaction> incompleteTransactions = new ArrayList<>();
        
        for (Transaction transaction : allTransactions) {
            if (transaction.getStatus() == domain.TransactionStatus.PENDING) {
                incompleteTransactions.add(transaction);
            }
        }
        
        System.out.println("RecoveryService: Found " + incompleteTransactions.size() + " incomplete transactions for machine " + machineId);
        return incompleteTransactions;
    }
}

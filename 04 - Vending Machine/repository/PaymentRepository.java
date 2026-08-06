package repository;

import domain.Transaction;
import domain.Denomination;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PaymentRepository {
    private Map<Integer, Transaction> transactions;
    private Map<Integer, Map<Denomination, Integer>> cashBoxes; // machineId -> cashBox
    private int nextTransactionId;

    public PaymentRepository() {
        this.transactions = new HashMap<>();
        this.cashBoxes = new HashMap<>();
        this.nextTransactionId = 1;
        System.out.println("PaymentRepository initialized");
    }

    public void saveTransaction(Transaction transaction) {
        if (transaction.getId() == 0) {
            // Set generated ID
            transaction.setId(nextTransactionId++);
        }
        transactions.put(transaction.getId(), transaction);
        System.out.println("Repository: Saved transaction with ID: " + transaction.getId());
    }

    public Transaction findById(int transactionId) {
        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            System.out.println("Repository: Transaction not found with ID: " + transactionId);
        } else {
            System.out.println("Repository: Found transaction with ID: " + transactionId);
        }
        return transaction;
    }

    public List<Transaction> findByMachine(int machineId) {
        List<Transaction> machineTransactions = transactions.values().stream()
            .filter(t -> t.getVendingMachineId() == machineId)
            .collect(Collectors.toList());
        System.out.println("Repository: Found " + machineTransactions.size() + " transactions for machine " + machineId);
        return machineTransactions;
    }

    public void updateCashBox(int machineId, Map<Denomination, Integer> denominations) {
        cashBoxes.put(machineId, new HashMap<>(denominations));
        System.out.println("Repository: Updated cash box for machine " + machineId);
    }

    public Map<Denomination, Integer> getCashBox(int machineId) {
        Map<Denomination, Integer> cashBox = cashBoxes.get(machineId);
        if (cashBox == null) {
            // Initialize empty cash box if not exists
            cashBox = new HashMap<>();
            cashBoxes.put(machineId, cashBox);
        }
        return new HashMap<>(cashBox);
    }

    public List<Transaction> getTransactionHistory(int machineId) {
        return findByMachine(machineId);
    }

    public int getTotalTransactions() {
        return transactions.size();
    }

    public double getTotalCashInMachine(int machineId) {
        Map<Denomination, Integer> cashBox = getCashBox(machineId);
        double total = 0.0;
        for (Map.Entry<Denomination, Integer> entry : cashBox.entrySet()) {
            total += entry.getKey().getValueInDollars() * entry.getValue();
        }
        return total;
    }
}

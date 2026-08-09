package repository.impl;

import domain.Transaction;
import repository.TransactionRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TransactionRepositoryImpl implements TransactionRepository {
    private Map<String, Transaction> transactions = new ConcurrentHashMap<>();
    private Map<String, String> providerRefToId = new ConcurrentHashMap<>(); // providerRef -> transactionId

    @Override
    public Transaction save(Transaction transaction) {
        transactions.put(transaction.getId(), transaction);
        if (transaction.getProviderRef() != null && !transaction.getProviderRef().isEmpty()) {
            providerRefToId.put(transaction.getProviderRef(), transaction.getId());
        }
        return transaction;
    }

    @Override
    public Optional<Transaction> findByProviderRef(String providerRef) {
        String transactionId = providerRefToId.get(providerRef);
        if (transactionId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(transactions.get(transactionId));
    }
}

package controller;

import domain.Transaction;
import domain.TransactionStatus;
import service.TransactionService;

public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Transaction initiateTransaction(String bookingId) {
        return transactionService.initiateTransaction(bookingId);
    }

    public void handleTransactionCallback(String providerRef, TransactionStatus status) {
        transactionService.handleCallback(providerRef, status);
    }
}

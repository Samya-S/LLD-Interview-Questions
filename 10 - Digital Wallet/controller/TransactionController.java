package controller;

import domain.Transaction;
import domain.TransactionStatus;
import service.TransactionService;

import java.util.Map;

public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Transaction transfer(String fromAccountNumber, String toAccountNumber, long amountMinor, String description) {
        return transactionService.transfer(fromAccountNumber, toAccountNumber, amountMinor, description);
    }

    public Transaction initiateDeposit(String accountNumber, long amountMinor, String paymentMethod,
                                       String paymentGateway, Map<String, String> paymentDetails) {
        return transactionService.initiateDeposit(accountNumber, amountMinor, paymentMethod, paymentGateway, paymentDetails);
    }

    public void handlePaymentCallback(String providerRef, TransactionStatus status) {
        transactionService.handleDepositCallback(providerRef, status);
    }

    public Transaction withdraw(String accountNumber, long amountMinor, String description) {
        return transactionService.withdraw(accountNumber, amountMinor, description);
    }
}


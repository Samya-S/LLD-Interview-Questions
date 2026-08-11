package controller;

import domain.AccountStatement;
import domain.Wallet;
import service.WalletService;
import service.TransactionService;

public class WalletController {
    private WalletService walletService;
    private TransactionService transactionService;

    public WalletController(WalletService walletService, TransactionService transactionService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    public Wallet createWallet(String userId) {
        return walletService.createWallet(userId);
    }

    public long getBalance(String accountNumber) {
        return walletService.getByAccountNumber(accountNumber).getBalanceMinor();
    }

    public AccountStatement getStatement(String accountNumber, Long startUtc, Long endUtc) {
        return transactionService.getStatement(accountNumber, startUtc, endUtc);
    }
}


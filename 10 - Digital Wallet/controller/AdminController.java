package controller;

import domain.Wallet;
import service.WalletService;

public class AdminController {
    private WalletService walletService;

    public AdminController(WalletService walletService) {
        this.walletService = walletService;
    }

    public Wallet suspendWallet(String accountNumber) {
        return walletService.suspendWallet(accountNumber);
    }

    public Wallet closeWallet(String accountNumber) {
        return walletService.closeWallet(accountNumber);
    }

    public Wallet reopenWallet(String accountNumber) {
        return walletService.reopenWallet(accountNumber);
    }
}


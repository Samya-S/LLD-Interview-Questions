package service;

import domain.Wallet;
import domain.WalletStatus;
import repository.WalletRepository;

import java.util.Optional;
import java.util.UUID;

public class WalletService {
    private WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet createWallet(String userId) {
        String id = UUID.randomUUID().toString();
        String accountNumber = "ACC_" + id.substring(0, 8);
        long now = System.currentTimeMillis();
        Wallet wallet = new Wallet(id, accountNumber, 0L, userId, WalletStatus.ACTIVE, now, now);
        return walletRepository.save(wallet);
    }

    public Wallet getByAccountNumber(String accountNumber) {
        Optional<Wallet> walletOpt = walletRepository.findByAccountNumber(accountNumber);
        if (walletOpt.isEmpty()) {
            throw new IllegalArgumentException("Wallet not found for account: " + accountNumber);
        }
        return walletOpt.get();
    }

    public boolean isActive(String accountNumber) {
        return walletRepository.findByAccountNumber(accountNumber)
                .map(w -> w.getStatus() == WalletStatus.ACTIVE)
                .orElse(false);
    }

    public Wallet suspendWallet(String accountNumber) {
        Wallet wallet = getByAccountNumber(accountNumber);
        wallet.setStatus(WalletStatus.SUSPENDED);
        wallet.setUpdatedAt(System.currentTimeMillis());
        return walletRepository.save(wallet);
    }

    public Wallet closeWallet(String accountNumber) {
        Wallet wallet = getByAccountNumber(accountNumber);
        wallet.setStatus(WalletStatus.CLOSED);
        wallet.setUpdatedAt(System.currentTimeMillis());
        return walletRepository.save(wallet);
    }

    public Wallet reopenWallet(String accountNumber) {
        Wallet wallet = getByAccountNumber(accountNumber);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setUpdatedAt(System.currentTimeMillis());
        return walletRepository.save(wallet);
    }
}


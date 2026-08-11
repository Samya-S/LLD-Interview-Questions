package repository.impl;

import domain.Wallet;
import repository.WalletRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WalletRepositoryImpl implements WalletRepository {
    private Map<String, Wallet> walletsById = new ConcurrentHashMap<>();
    private Map<String, String> walletIdByAccountNumber = new ConcurrentHashMap<>();

    @Override
    public Wallet save(Wallet wallet) {
        walletsById.put(wallet.getId(), wallet);
        walletIdByAccountNumber.put(wallet.getAccountNumber(), wallet.getId());
        return wallet;
    }

    @Override
    public Optional<Wallet> findById(String walletId) {
        return Optional.ofNullable(walletsById.get(walletId));
    }

    @Override
    public Optional<Wallet> findByAccountNumber(String accountNumber) {
        String id = walletIdByAccountNumber.get(accountNumber);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(walletsById.get(id));
    }
}


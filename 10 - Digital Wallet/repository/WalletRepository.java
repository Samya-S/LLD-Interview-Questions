package repository;

import domain.Wallet;
import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(String walletId);
    Optional<Wallet> findByAccountNumber(String accountNumber);
}


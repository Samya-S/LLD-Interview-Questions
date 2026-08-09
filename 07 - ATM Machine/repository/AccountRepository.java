package repository;

import domain.Account;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(String accountId);

    void updateBalance(String accountId, long newBalance);

    void updateDailyWithdrawalUsed(String accountId, long amountUsed);
}

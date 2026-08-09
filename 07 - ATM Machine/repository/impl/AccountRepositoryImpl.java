package repository.impl;

import domain.Account;
import repository.AccountRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepositoryImpl implements AccountRepository {
    private Map<String, Account> accountStore = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        accountStore.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accountStore.get(accountId));
    }

    @Override
    public void updateBalance(String accountId, long newBalance) {
        Optional<Account> accountOpt = findById(accountId);
        if (accountOpt.isPresent()) {
            accountOpt.get().setBalanceMinorUnits(newBalance);
        }
    }

    @Override
    public void updateDailyWithdrawalUsed(String accountId, long amountUsed) {
        Optional<Account> accountOpt = findById(accountId);
        if (accountOpt.isPresent()) {
            accountOpt.get().setDailyWithdrawalUsedMinor(amountUsed);
        }
    }
}

package domain;

import java.util.List;

public class AccountStatement {
    private String walletId;
    private String walletAccountNumber;
    private List<Transaction> transactions;
    private Long startDateUtc; // optional
    private Long endDateUtc;   // optional
    private long currentBalanceMinor;

    public AccountStatement() {
    }

    public AccountStatement(String walletId, String walletAccountNumber, List<Transaction> transactions,
                            Long startDateUtc, Long endDateUtc, long currentBalanceMinor) {
        this.walletId = walletId;
        this.walletAccountNumber = walletAccountNumber;
        this.transactions = transactions;
        this.startDateUtc = startDateUtc;
        this.endDateUtc = endDateUtc;
        this.currentBalanceMinor = currentBalanceMinor;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getWalletAccountNumber() {
        return walletAccountNumber;
    }

    public void setWalletAccountNumber(String walletAccountNumber) {
        this.walletAccountNumber = walletAccountNumber;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Long getStartDateUtc() {
        return startDateUtc;
    }

    public void setStartDateUtc(Long startDateUtc) {
        this.startDateUtc = startDateUtc;
    }

    public Long getEndDateUtc() {
        return endDateUtc;
    }

    public void setEndDateUtc(Long endDateUtc) {
        this.endDateUtc = endDateUtc;
    }

    public long getCurrentBalanceMinor() {
        return currentBalanceMinor;
    }

    public void setCurrentBalanceMinor(long currentBalanceMinor) {
        this.currentBalanceMinor = currentBalanceMinor;
    }
}


package domain;

import java.util.Map;

public class Transaction {
    private String id;
    private String atmId;
    private String sessionId;
    private String accountId;
    private TransactionType type;
    private long amountMinorUnits;
    private TransactionStatus status;
    private Map<Denomination, Integer> dispensedNotes;
    private Map<Denomination, Integer> depositedNotes;
    private long createdAt;
    private long timeoutAt;

    public Transaction(String id, String atmId, String sessionId, String accountId,
            TransactionType type, long amountMinorUnits) {
        this.id = id;
        this.atmId = atmId;
        this.sessionId = sessionId;
        this.accountId = accountId;
        this.type = type;
        this.amountMinorUnits = amountMinorUnits;
        this.status = TransactionStatus.PENDING;
        this.createdAt = System.currentTimeMillis();
        this.timeoutAt = createdAt + 300000; // 5 minutes timeout
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAtmId() {
        return atmId;
    }

    public void setAtmId(String atmId) {
        this.atmId = atmId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public long getAmountMinorUnits() {
        return amountMinorUnits;
    }

    public void setAmountMinorUnits(long amountMinorUnits) {
        this.amountMinorUnits = amountMinorUnits;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Map<Denomination, Integer> getDispensedNotes() {
        return dispensedNotes;
    }

    public void setDispensedNotes(Map<Denomination, Integer> dispensedNotes) {
        this.dispensedNotes = dispensedNotes;
    }

    public Map<Denomination, Integer> getDepositedNotes() {
        return depositedNotes;
    }

    public void setDepositedNotes(Map<Denomination, Integer> depositedNotes) {
        this.depositedNotes = depositedNotes;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getTimeoutAt() {
        return timeoutAt;
    }

    public void setTimeoutAt(long timeoutAt) {
        this.timeoutAt = timeoutAt;
    }
}

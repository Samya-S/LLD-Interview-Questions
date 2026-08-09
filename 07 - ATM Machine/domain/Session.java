package domain;

public class Session {
    private String id;
    private String atmId;
    private String cardId;
    private String accountId;
    private long startTime;
    private long endTime;
    private boolean isActive;
    private String currentTransactionId;
    private TransactionType transactionType;
    private long amount;

    public Session(String id, String atmId, String cardId, String accountId) {
        this.id = id;
        this.atmId = atmId;
        this.cardId = cardId;
        this.accountId = accountId;
        this.startTime = System.currentTimeMillis();
        this.isActive = true;
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

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCurrentTransactionId() {
        return currentTransactionId;
    }

    public void setCurrentTransactionId(String currentTransactionId) {
        this.currentTransactionId = currentTransactionId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void endSession() {
        this.isActive = false;
        this.endTime = System.currentTimeMillis();
    }
}

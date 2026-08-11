package domain;

public class Transaction {
    private String id;
    private String fromWalletId; // nullable for DEPOSIT
    private String toWalletId;   // nullable for WITHDRAWAL
    private long amountMinor;
    private TransactionType type;
    private TransactionStatus status;
    private String providerRef; // nullable; for DEPOSIT
    private String description;
    private long timestamp;

    public Transaction() {
    }

    public Transaction(String id, String fromWalletId, String toWalletId, long amountMinor,
                       TransactionType type, TransactionStatus status, String providerRef,
                       String description, long timestamp) {
        this.id = id;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
        this.amountMinor = amountMinor;
        this.type = type;
        this.status = status;
        this.providerRef = providerRef;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromWalletId() {
        return fromWalletId;
    }

    public void setFromWalletId(String fromWalletId) {
        this.fromWalletId = fromWalletId;
    }

    public String getToWalletId() {
        return toWalletId;
    }

    public void setToWalletId(String toWalletId) {
        this.toWalletId = toWalletId;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public void setAmountMinor(long amountMinor) {
        this.amountMinor = amountMinor;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getProviderRef() {
        return providerRef;
    }

    public void setProviderRef(String providerRef) {
        this.providerRef = providerRef;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", fromWalletId='" + fromWalletId + '\'' +
                ", toWalletId='" + toWalletId + '\'' +
                ", amountMinor=" + amountMinor +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}


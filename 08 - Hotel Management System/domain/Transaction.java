package domain;

public class Transaction {
    private String id;
    private String bookingId;
    private long amountMinor;
    private String currency;
    private TransactionStatus status;
    private String providerRef;
    private long createdAt;
    private Long completedAt; // nullable
    private Long refundedAt; // nullable

    public Transaction() {
    }

    public Transaction(String id, String bookingId, long amountMinor, String currency,
            TransactionStatus status, String providerRef, long createdAt,
            Long completedAt, Long refundedAt) {
        this.id = id;
        this.bookingId = bookingId;
        this.amountMinor = amountMinor;
        this.currency = currency;
        this.status = status;
        this.providerRef = providerRef;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.refundedAt = refundedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    public void setAmountMinor(long amountMinor) {
        this.amountMinor = amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Long completedAt) {
        this.completedAt = completedAt;
    }

    public Long getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(Long refundedAt) {
        this.refundedAt = refundedAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", amountMinor=" + amountMinor +
                ", status=" + status +
                '}';
    }
}

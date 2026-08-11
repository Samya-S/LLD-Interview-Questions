package domain;

public class Wallet {
    private String id;
    private String accountNumber;
    private long balanceMinor;
    private String userId;
    private WalletStatus status;
    private long createdAt;
    private long updatedAt;

    public Wallet() {
    }

    public Wallet(String id, String accountNumber, long balanceMinor, String userId, WalletStatus status, long createdAt, long updatedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balanceMinor = balanceMinor;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public long getBalanceMinor() {
        return balanceMinor;
    }

    public void setBalanceMinor(long balanceMinor) {
        this.balanceMinor = balanceMinor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id='" + id + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", balanceMinor=" + balanceMinor +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                '}';
    }
}


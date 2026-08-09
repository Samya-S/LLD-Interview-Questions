package domain;

public class Card {
    private String id;
    private String accountId;
    private String expiry;
    private boolean isBlocked;
    private int pinRetriesLeft;

    public Card(String id, String accountId, String expiry) {
        this.id = id;
        this.accountId = accountId;
        this.expiry = expiry;
        this.isBlocked = false;
        this.pinRetriesLeft = 3;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public int getPinRetriesLeft() {
        return pinRetriesLeft;
    }

    public void setPinRetriesLeft(int pinRetriesLeft) {
        this.pinRetriesLeft = pinRetriesLeft;
    }

    public void decrementPinRetries() {
        pinRetriesLeft--;
        if (pinRetriesLeft <= 0) {
            isBlocked = true;
        }
    }

    public void resetPinRetries() {
        pinRetriesLeft = 3;
    }
}

package domain;

public class Account {
    private String id;
    private String holderName;
    private long balanceMinorUnits;
    private long dailyWithdrawalLimitMinor;
    private long dailyWithdrawalUsedMinor;
    private boolean isActive;

    public Account(String id, String holderName, long balanceMinorUnits) {
        this.id = id;
        this.holderName = holderName;
        this.balanceMinorUnits = balanceMinorUnits;
        this.dailyWithdrawalLimitMinor = 50000; // $500 default limit
        this.dailyWithdrawalUsedMinor = 0;
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public long getBalanceMinorUnits() {
        return balanceMinorUnits;
    }

    public void setBalanceMinorUnits(long balanceMinorUnits) {
        this.balanceMinorUnits = balanceMinorUnits;
    }

    public long getDailyWithdrawalLimitMinor() {
        return dailyWithdrawalLimitMinor;
    }

    public void setDailyWithdrawalLimitMinor(long dailyWithdrawalLimitMinor) {
        this.dailyWithdrawalLimitMinor = dailyWithdrawalLimitMinor;
    }

    public long getDailyWithdrawalUsedMinor() {
        return dailyWithdrawalUsedMinor;
    }

    public void setDailyWithdrawalUsedMinor(long dailyWithdrawalUsedMinor) {
        this.dailyWithdrawalUsedMinor = dailyWithdrawalUsedMinor;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

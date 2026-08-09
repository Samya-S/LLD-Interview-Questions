package domain;

public class CancellationPolicy {
    private String id;
    private String name; // NON_REFUNDABLE, PARTIAL, FLEX
    private int refundPercent; // 0-100
    private int cutoffHoursBeforeCheckIn;
    private long createdAt;

    public CancellationPolicy() {
    }

    public CancellationPolicy(String id, String name, int refundPercent, int cutoffHoursBeforeCheckIn, long createdAt) {
        this.id = id;
        this.name = name;
        this.refundPercent = refundPercent;
        this.cutoffHoursBeforeCheckIn = cutoffHoursBeforeCheckIn;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRefundPercent() {
        return refundPercent;
    }

    public void setRefundPercent(int refundPercent) {
        this.refundPercent = refundPercent;
    }

    public int getCutoffHoursBeforeCheckIn() {
        return cutoffHoursBeforeCheckIn;
    }

    public void setCutoffHoursBeforeCheckIn(int cutoffHoursBeforeCheckIn) {
        this.cutoffHoursBeforeCheckIn = cutoffHoursBeforeCheckIn;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CancellationPolicy{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", refundPercent=" + refundPercent +
                ", cutoffHoursBeforeCheckIn=" + cutoffHoursBeforeCheckIn +
                '}';
    }
}

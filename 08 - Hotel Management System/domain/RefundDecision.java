package domain;

public class RefundDecision {
    private int refundPercent;
    private long refundAmountMinor;

    public RefundDecision() {
    }

    public RefundDecision(int refundPercent, long refundAmountMinor) {
        this.refundPercent = refundPercent;
        this.refundAmountMinor = refundAmountMinor;
    }

    public int getRefundPercent() {
        return refundPercent;
    }

    public void setRefundPercent(int refundPercent) {
        this.refundPercent = refundPercent;
    }

    public long getRefundAmountMinor() {
        return refundAmountMinor;
    }

    public void setRefundAmountMinor(long refundAmountMinor) {
        this.refundAmountMinor = refundAmountMinor;
    }
}

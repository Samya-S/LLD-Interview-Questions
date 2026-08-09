package service;

import domain.Booking;
import domain.CancellationPolicy;
import domain.RefundDecision;

public class PolicyService {
    public RefundDecision evaluateCancellation(Booking booking, CancellationPolicy policy, long nowUtc) {
        long hoursUntilCheckIn = (booking.getCheckInDateUtc() - nowUtc) / (1000 * 60 * 60);

        if (hoursUntilCheckIn < policy.getCutoffHoursBeforeCheckIn()) {
            // Past cutoff, no refund
            return new RefundDecision(0, 0);
        }

        int refundPercent = policy.getRefundPercent();
        long refundAmount = (booking.getTotalAmountMinor() * refundPercent) / 100;

        return new RefundDecision(refundPercent, refundAmount);
    }
}

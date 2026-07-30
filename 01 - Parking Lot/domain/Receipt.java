package domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Receipt {
    private UUID id;
    private UUID ticketId;
    private LocalDateTime exitTime;
    private double totalFee;
    private PaymentStatus paymentStatus;

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED
    }

    public Receipt(UUID ticketId, double totalFee) {
        this.id = UUID.randomUUID();
        this.ticketId = ticketId;
        this.exitTime = LocalDateTime.now();
        this.totalFee = totalFee;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    public void markAsPaid() {
        this.paymentStatus = PaymentStatus.SUCCESS;
    }

    public void markAsFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", exitTime=" + exitTime +
                ", totalFee=" + totalFee +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
} 
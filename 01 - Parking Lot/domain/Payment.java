package domain;

import java.util.UUID;

public class Payment {
    private UUID id;
    private UUID ticketId;
    private double amount;
    private PaymentGateway gateway;
    private PaymentStatus status;

    public enum PaymentGateway {
        RAZORPAY, STRIPE
    }

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED
    }

    public Payment(UUID ticketId, double amount, PaymentGateway gateway) {
        this.id = UUID.randomUUID();
        this.ticketId = ticketId;
        this.amount = amount;
        this.gateway = gateway;
        this.status = PaymentStatus.PENDING;
    }

    public void markAsSuccess() {
        this.status = PaymentStatus.SUCCESS;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentGateway getGateway() {
        return gateway;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", amount=" + amount +
                ", gateway=" + gateway +
                ", status=" + status +
                '}';
    }
}
package controller;

import domain.PaymentStatus;
import service.RideService;

public class PaymentController {
    private final RideService rideService;

    public PaymentController(RideService rideService) {
        this.rideService = rideService;
    }

    public void handleCallback(String transactionId, PaymentStatus status) {
        rideService.handlePaymentCallback(transactionId, status);
    }
}

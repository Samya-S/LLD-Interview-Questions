package service.gateway;

import java.util.Map;

public interface PaymentGatewayProvider {
    String getName();
    String initiatePayment(String accountNumber, long amountMinor, String paymentMethod, Map<String, String> paymentDetails);
    boolean verifyCallback(String providerRef, String status);
}


package service.gateway;

import java.util.Map;
import java.util.UUID;

public class MockPaymentGatewayProvider implements PaymentGatewayProvider {
    @Override
    public String getName() {
        return "mock";
    }

    @Override
    public String initiatePayment(String accountNumber, long amountMinor, String paymentMethod, Map<String, String> paymentDetails) {
        // TODO: Integrate with actual PG
        return "PG_REF_" + UUID.randomUUID();
    }

    @Override
    public boolean verifyCallback(String providerRef, String status) {
        // TODO: Verify signature/callback with PG
        return true;
    }
}


package service.gateway;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentGatewayRouter {
    private final Map<String, PaymentGatewayProvider> providers = new ConcurrentHashMap<>();

    public void register(PaymentGatewayProvider provider) {
        providers.put(provider.getName().toLowerCase(), provider);
    }

    public String selectProvider(String preferredGateway, long amountMinor, String currency) {
        // Keep it simple: prefer requested, else any registered one
        if (preferredGateway != null) {
            String key = preferredGateway.toLowerCase();
            if (providers.containsKey(key)) {
                return key;
            }
        }
        return providers.keySet().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No payment providers registered"));
    }

    public PaymentGatewayProvider resolve(String gatewayName) {
        PaymentGatewayProvider provider = providers.get(gatewayName.toLowerCase());
        if (provider == null) {
            throw new IllegalArgumentException("Payment provider not registered: " + gatewayName);
        }
        return provider;
    }
}


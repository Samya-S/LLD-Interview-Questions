package repository;

import domain.Payment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PaymentRepository {
    private Map<UUID, Payment> payments = new ConcurrentHashMap<>();
    private Map<UUID, List<UUID>> ticketToPayments = new ConcurrentHashMap<>();

    public Payment save(Payment payment) {
        payments.put(payment.getId(), payment);
        ticketToPayments.computeIfAbsent(payment.getTicketId(), k -> new ArrayList<>())
                       .add(payment.getId());
        return payment;
    }

    public Optional<Payment> findById(UUID paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    public List<Payment> findByTicketId(UUID ticketId) {
        List<UUID> paymentIds = ticketToPayments.get(ticketId);
        if (paymentIds != null) {
            return paymentIds.stream()
                    .map(payments::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    public void update(Payment payment) {
        if (payments.containsKey(payment.getId())) {
            payments.put(payment.getId(), payment);
        }
    }

    public void delete(UUID paymentId) {
        Payment payment = payments.remove(paymentId);
        if (payment != null) {
            List<UUID> ticketPayments = ticketToPayments.get(payment.getTicketId());
            if (ticketPayments != null) {
                ticketPayments.remove(paymentId);
            }
        }
    }

    public void clear() {
        payments.clear();
        ticketToPayments.clear();
    }
} 
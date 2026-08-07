package repository;

import domain.DeliveryStatus;
import domain.MessageDelivery;
import java.util.List;

public interface MessageDeliveryRepository {
    MessageDelivery save(MessageDelivery delivery);
    List<MessageDelivery> findPendingBySubscriber(String subscriberId);
    void updateDeliveryStatus(String deliveryId, DeliveryStatus status);
    void deleteById(String deliveryId);
}

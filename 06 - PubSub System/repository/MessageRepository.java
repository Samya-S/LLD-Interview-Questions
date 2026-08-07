package repository;

import domain.Message;
import java.util.Optional;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findById(String messageId);
    void deleteById(String messageId);
}

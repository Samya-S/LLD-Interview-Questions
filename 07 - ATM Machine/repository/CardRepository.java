package repository;

import domain.Card;
import java.util.Optional;

public interface CardRepository {
    Card save(Card card);

    Optional<Card> findById(String cardId);

    void updatePinRetries(String cardId, int retriesLeft);

    void blockCard(String cardId);
}

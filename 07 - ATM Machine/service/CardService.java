package service;

import domain.Card;
import repository.CardRepository;

public class CardService {
    private CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public boolean validateCard(String cardId) {
        // TODO: Validate card with bank server and save it in repo, use caching here for some minutes
        Card card = cardRepository.findById(cardId).orElse(null);
        return card != null && !card.isBlocked();
    }

    public void ejectCard(String atmId) {
        // TODO: Delete from cache, or where the ATM is saving 
        System.out.println("Card ejected from ATM: " + atmId);
    }

    public boolean authenticateCard(String cardId, String pin) {
        Card card = cardRepository.findById(cardId).orElse(null);
        if (card == null || card.isBlocked()) {
            return false;
        }

        // TODO: Validate PIN with bank server
        boolean isValidPin = true; // Placeholder for demo

        if (isValidPin) {
            card.resetPinRetries();
        } else {
            card.decrementPinRetries();
        }
        
        cardRepository.save(card);
        return isValidPin;
    }
}

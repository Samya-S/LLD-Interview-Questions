package domain.state;

import domain.ATM;
import service.CardService;
import service.SessionService;

public class IdleState extends AbstractATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        System.out.println("[IdleState] insertCard: " + cardId);
        // TODO: CardService.validateCard(cardId)
        CardService cardService = atm.getCardService();
        if (cardService != null) {
            // boolean valid = cardService.validateCard(cardId);
            System.out.println("[CardService] validate card and cache details");
        }
        SessionService sessionService = atm.getSessionService();
        if (sessionService != null) {
            System.out.println("[SessionService] start session");
            atm.setCurrentSession(sessionService.startSession(atm.getId(), cardId));
        }
    }

    @Override
    public ATMState next(ATM atm) {
        // After insert card → CardInsertedState
        return atm.getCurrentSession() != null ? new CardInsertedState() : this;
    }
}
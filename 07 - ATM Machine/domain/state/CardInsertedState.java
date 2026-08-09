package domain.state;

import domain.ATM;
import domain.exception.InvalidATMOperationException;
import service.CardService;
import service.SessionService;

public class CardInsertedState extends AbstractATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("Card already inserted");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("[CardInsertedState] ejectCard");
        SessionService sessionService = atm.getSessionService();
        if (sessionService != null && atm.getCurrentSession() != null) {
            System.out.println("[SessionService] end session");
            sessionService.endSession(atm.getCurrentSession().getId());
            atm.setCurrentSession(null);
        }
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        System.out.println("[CardInsertedState] enterPin");
        CardService cardService = atm.getCardService();
        boolean ok = true;
        if (cardService != null && atm.getCurrentSession() != null) {
            System.out.println("[CardService] authenticate card");
            ok = cardService.authenticateCard(atm.getCurrentSession().getCardId(), pin);
        }
        if (!ok) {
            throw new InvalidATMOperationException("Authentication failed");
        }
    }

    @Override
    public void endSession(ATM atm) {
        System.out.println("[CardInsertedState] endSession");
        SessionService sessionService = atm.getSessionService();
        if (sessionService != null && atm.getCurrentSession() != null) {
            sessionService.endSession(atm.getCurrentSession().getId());
            atm.setCurrentSession(null);
        }
    }

    @Override
    public ATMState next(ATM atm) {
        if (atm.getCurrentSession() == null)
            return new IdleState();
        return new AuthenticatedState();
    }
}
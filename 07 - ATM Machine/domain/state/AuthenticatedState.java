package domain.state;

import domain.ATM;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;
import service.SessionService;

public class AuthenticatedState extends AbstractATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("Card already inserted");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("[AuthenticatedState] ejectCard");
        endSession(atm);
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new InvalidATMOperationException("Already authenticated");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        System.out.println("[AuthenticatedState] selectTransaction: " + type);
        if (atm.getCurrentSession() != null) {
            atm.getCurrentSession().setTransactionType(type);
        }
    }

    @Override
    public void endSession(ATM atm) {
        System.out.println("[AuthenticatedState] endSession");
        SessionService sessionService = atm.getSessionService();
        if (sessionService != null && atm.getCurrentSession() != null) {
            sessionService.endSession(atm.getCurrentSession().getId());
            atm.setCurrentSession(null);
        }
    }

    @Override
    public ATMState next(ATM atm) {
        // After selectTransaction → TransactionSelected, after end/eject → Idle
        if (atm.getCurrentSession() == null)
            return new IdleState();
        if (atm.getCurrentSession().getTransactionType() != null)
            return new TransactionSelectedState();
        return this;
    }
}
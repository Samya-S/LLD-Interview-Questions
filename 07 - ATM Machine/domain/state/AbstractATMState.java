package domain.state;

import domain.ATM;
import domain.TransactionType;
import domain.exception.InvalidATMOperationException;

public abstract class AbstractATMState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("Operation not allowed in " + getClass().getSimpleName());
    }

    @Override
    public void ejectCard(ATM atm) {
        throw new InvalidATMOperationException("Operation not allowed in " + getClass().getSimpleName());
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new InvalidATMOperationException("Operation not allowed in " + getClass().getSimpleName());
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        throw new InvalidATMOperationException("Operation not allowed in " + getClass().getSimpleName());
    }

    @Override
    public void processTransaction(ATM atm, long amount) {
        throw new InvalidATMOperationException("Operation not allowed in " + getClass().getSimpleName());
    }

    @Override
    public void endSession(ATM atm) {
        throw new InvalidATMOperationException("Operation not allowed in " + getClass().getSimpleName());
    }

    @Override
    public ATMState next(ATM atm) {
        // Default: remain in current state
        return this;
    }
}
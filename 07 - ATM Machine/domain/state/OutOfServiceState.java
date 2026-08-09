package domain.state;

import domain.ATM;
import domain.exception.InvalidATMOperationException;

public class OutOfServiceState extends AbstractATMState {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("ATM is out of service");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("[OutOfServiceState] ejectCard (if any)");
    }
}
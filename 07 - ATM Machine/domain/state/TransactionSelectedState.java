package domain.state;

import domain.ATM;
import domain.TransactionType;
import domain.Denomination;
import domain.Transaction;
import domain.TransactionStatus;
import domain.exception.InvalidATMOperationException;
import service.TransactionService;

public class TransactionSelectedState extends AbstractATMState implements SupportsNotes {
    @Override
    public void insertCard(ATM atm, String cardId) {
        throw new InvalidATMOperationException("Card already inserted");
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("[TransactionSelectedState] ejectCard");
        atm.setCurrentState(new IdleState());
    }

    @Override
    public void enterPin(ATM atm, String pin) {
        throw new InvalidATMOperationException("Already authenticated");
    }

    @Override
    public void selectTransaction(ATM atm, TransactionType type) {
        System.out.println("[TransactionSelectedState] update transaction: " + type);
        if (atm.getCurrentSession() != null) {
            atm.getCurrentSession().setTransactionType(type);
        }
    }

    @Override
    public void processTransaction(ATM atm, long amount) {
        TransactionService txnService = atm.getTransactionService();
        TransactionType type = atm.getCurrentSession() != null ? atm.getCurrentSession().getTransactionType() : null;
        if (txnService == null || type == null) {
            throw new InvalidATMOperationException("Transaction not initialized");
        }
        System.out.println("[TransactionSelectedState] process: type=" + type + ", amount=" + amount);
        Transaction txn;
        switch (type) {
            case BALANCE:
                txn = txnService.showBalance(atm.getCurrentSession().getId());
                break;
            case WITHDRAW:
                txn = txnService.withdrawCash(atm.getCurrentSession().getId(), amount);
                break;
            case DEPOSIT:
                txn = txnService.depositCash(atm.getCurrentSession().getId(), java.util.Collections.emptyMap());
                break;
            default:
                throw new InvalidATMOperationException("Unsupported transaction type");
        }
        atm.setLastTransaction(txn);
    }

    @Override
    public void processTransaction(ATM atm, long amount, java.util.Map<Denomination, Integer> notes) {
        TransactionService txnService = atm.getTransactionService();
        TransactionType type = atm.getCurrentSession() != null ? atm.getCurrentSession().getTransactionType() : null;
        if (txnService == null || type == null) {
            throw new InvalidATMOperationException("Transaction not initialized");
        }
        System.out.println("[TransactionSelectedState] process with notes: type=" + type + ", amount=" + amount);
        Transaction txn;
        if (type == TransactionType.DEPOSIT) {
            txn = txnService.depositCash(atm.getCurrentSession().getId(), notes);
        } else {
            processTransaction(atm, amount);
            return;
        }
        atm.setLastTransaction(txn);
    }

    @Override
    public void endSession(ATM atm) {
        System.out.println("[TransactionSelectedState] endSession");
        atm.setCurrentState(new IdleState());
    }

    @Override
    public ATMState next(ATM atm) {
        if (atm.getLastTransaction() != null && atm.getLastTransaction().getStatus() == TransactionStatus.SUCCESS) {
            return new TransactionCompletedState();
        }
        if (atm.getCurrentSession() == null)
            return new IdleState();
        return this;
    }
}
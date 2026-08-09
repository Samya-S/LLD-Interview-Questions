package domain.strategy;

import domain.*;
import java.util.Map;

public class DepositStrategy implements TransactionStrategy {
    @Override
    public Transaction processTransaction(String sessionId, long amount, Map<Denomination, Integer> notes) {
        System.out.println("[DepositStrategy] Processing deposit: " + amount + " for session: " + sessionId);

        Transaction transaction = new Transaction(
                "TXN_" + System.currentTimeMillis(),
                "ATM_001", // TODO: Get from session
                sessionId,
                "ACC_001", // TODO: Get from session
                TransactionType.DEPOSIT,
                amount);

        // TODO: Validate deposited notes
        // TODO: Update account balance and ATM cash inventory

        if (notes != null) {
            transaction.setDepositedNotes(notes);
        }
        transaction.setStatus(TransactionStatus.SUCCESS);

        System.out.println("[DepositStrategy] Deposit completed, deposited: " + notes);

        return transaction;
    }
}

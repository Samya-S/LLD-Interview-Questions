package domain.strategy;

import domain.*;
import java.util.Map;
import java.util.HashMap;

public class WithdrawalStrategy implements TransactionStrategy {
    @Override
    public Transaction processTransaction(String sessionId, long amount, Map<Denomination, Integer> notes) {
        System.out.println("[WithdrawalStrategy] Processing withdrawal: " + amount + " for session: " + sessionId);

        Transaction transaction = new Transaction(
                "TXN_" + System.currentTimeMillis(),
                "ATM_001", // TODO: Get from session
                sessionId,
                "ACC_001", // TODO: Get from session
                TransactionType.WITHDRAW,
                amount);

        // TODO: Check account balance, daily limits, ATM cash availability
        // TODO: Calculate optimal note combination
        // TODO: Update account balance and ATM cash

        Map<Denomination, Integer> dispensedNotes = calculateNotes(amount);
        transaction.setDispensedNotes(dispensedNotes);
        transaction.setStatus(TransactionStatus.SUCCESS);

        System.out.println("[WithdrawalStrategy] Withdrawal completed, dispensed: " + dispensedNotes);

        return transaction;
    }

    private Map<Denomination, Integer> calculateNotes(long amount) {
        Map<Denomination, Integer> notes = new HashMap<>();
        long remaining = amount;

        for (Denomination denom : Denomination.values()) {
            int count = (int) (remaining / denom.getValue());
            if (count > 0) {
                notes.put(denom, count);
                remaining -= count * denom.getValue();
            }
        }

        return notes;
    }
}

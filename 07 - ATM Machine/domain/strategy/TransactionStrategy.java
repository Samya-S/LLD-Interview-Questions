package domain.strategy;

import domain.Transaction;
import domain.Denomination;
import java.util.Map;

public interface TransactionStrategy {
    Transaction processTransaction(String sessionId, long amount, Map<Denomination, Integer> notes);
}

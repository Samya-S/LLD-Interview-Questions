package domain.state;

import domain.ATM;
import domain.Denomination;
import java.util.Map;

public interface SupportsNotes {
    void processTransaction(ATM atm, long amount, Map<Denomination, Integer> notes);
}
package repository;

import domain.CashDrawer;
import domain.Denomination;
import java.util.Map;
import java.util.Optional;

public interface CashDrawerRepository {
    CashDrawer save(CashDrawer cashDrawer);
    Optional<CashDrawer> findByATMId(String atmId);
    void updateCashInventory(String atmId, Map<Denomination, Integer> notes);
}

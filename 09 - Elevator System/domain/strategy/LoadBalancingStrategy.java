package domain.strategy;

import domain.Elevator;
import domain.ExternalRequest;
import java.util.List;

public class LoadBalancingStrategy implements ElevatorSelectionStrategy {
    
    @Override
    public Elevator selectElevator(ExternalRequest request, List<Elevator> availableElevators) {
        if (availableElevators.isEmpty()) {
            return null;
        }
        
        Elevator leastLoaded = null;
        int minLoad = Integer.MAX_VALUE;
        
        for (Elevator elevator : availableElevators) {
            if (!elevator.isAvailable()) continue;
            
            if (elevator.getCurrentLoad() < minLoad) {
                minLoad = elevator.getCurrentLoad();
                leastLoaded = elevator;
            }
        }
        
        return leastLoaded;
    }
}

package domain.strategy;

import domain.Elevator;
import domain.InternalRequest;
import java.util.ArrayList;
import java.util.List;

public class FCFSStrategy implements MovementStrategy {
    
    @Override
    public List<Integer> calculatePath(Elevator elevator, List<InternalRequest> requests) {
        List<Integer> path = new ArrayList<>();
        
        // Sort by timestamp (FCFS)
        requests.sort((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));
        
        for (InternalRequest request : requests) {
            if (!path.contains(request.getDestinationFloor())) {
                path.add(request.getDestinationFloor());
            }
        }
        
        return path;
    }
}

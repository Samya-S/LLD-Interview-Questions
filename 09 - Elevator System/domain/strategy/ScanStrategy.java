package domain.strategy;

import domain.Direction;
import domain.Elevator;
import domain.InternalRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScanStrategy implements MovementStrategy {
    
    @Override
    public List<Integer> calculatePath(Elevator elevator, List<InternalRequest> requests) {
        List<Integer> path = new ArrayList<>();
        
        List<Integer> floors = requests.stream()
                .map(InternalRequest::getDestinationFloor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        if (floors.isEmpty()) return path;
        
        int currentFloor = elevator.getCurrentFloor();
        Direction direction = elevator.getDirection();
        
        if (direction == Direction.IDLE) {
            direction = floors.get(0) > currentFloor ? Direction.UP : Direction.DOWN;
        }
        
        // SCAN: Continue in current direction until no more requests
        if (direction == Direction.UP) {
            // Add floors above current floor in ascending order
            floors.stream().filter(f -> f >= currentFloor).forEach(path::add);
            // Add floors below current floor in descending order
            floors.stream().filter(f -> f < currentFloor).sorted((a, b) -> b - a).forEach(path::add);
        } else {
            // Add floors below current floor in descending order
            floors.stream().filter(f -> f <= currentFloor).sorted((a, b) -> b - a).forEach(path::add);
            // Add floors above current floor in ascending order
            floors.stream().filter(f -> f > currentFloor).forEach(path::add);
        }
        
        return path;
    }
}

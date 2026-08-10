package domain.state;

import domain.Elevator;
import domain.ElevatorState;

/**
 * Concrete state for when elevator is in full maintenance mode
 * This state blocks all requests and only allows maintenance operations
 */
public class MaintenanceState implements ElevatorStateHandler {
    
    
    @Override
    public void openDoors(Elevator elevator) {
        // Allow door operations for maintenance access
        elevator.setState(ElevatorState.DOORS_OPENING);
        System.out.println("Maintenance elevator " + elevator.getId() + " opening doors for maintenance access");
    }
    
    @Override
    public void closeDoors(Elevator elevator) {
        // Allow door operations for maintenance access
        elevator.setState(ElevatorState.DOORS_CLOSING);
        System.out.println("Maintenance elevator " + elevator.getId() + " closing doors after maintenance access");
    }
    
    @Override
    public void enterMaintenance(Elevator elevator) {
        // Already in maintenance
        System.out.println("Elevator " + elevator.getId() + " already in maintenance mode");
    }
    
    @Override
    public void exitMaintenance(Elevator elevator) {
        // Exit maintenance and return to normal operation
        elevator.setState(ElevatorState.STOPPED);
        elevator.setStateHandler(new StoppedState());
        elevator.setActive(true);
        System.out.println("Elevator " + elevator.getId() + " exiting maintenance mode and returning to service");
    }
    
    @Override
    public boolean canAcceptExternalRequests(Elevator elevator) {
        // Block all external requests in maintenance
        return false;
    }
    
    @Override
    public boolean canAcceptInternalRequests(Elevator elevator) {
        // Block all internal requests in maintenance
        return false;
    }
    
    @Override
    public String getStateName() {
        return "MAINTENANCE";
    }
}

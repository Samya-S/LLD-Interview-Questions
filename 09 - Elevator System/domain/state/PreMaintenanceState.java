package domain.state;

import domain.Elevator;
import domain.ElevatorState;

/**
 * Concrete state for when elevator is preparing for maintenance
 * This state blocks new external requests but completes existing internal requests gracefully
 */
public class PreMaintenanceState implements ElevatorStateHandler {
    
    
    /**
     * Check if elevator should transition to full maintenance
     * This should be called by MovementService when processing is complete
     */
    public void checkForMaintenanceTransition(Elevator elevator) {
        // This method should be called by external services to check if we can transition
        System.out.println("Pre-maintenance elevator " + elevator.getId() + " has no more pending requests - entering full maintenance mode");
        elevator.setState(ElevatorState.MAINTENANCE);
        elevator.setStateHandler(new MaintenanceState());
        elevator.setActive(false);
    }
    
    @Override
    public void openDoors(Elevator elevator) {
        elevator.setState(ElevatorState.DOORS_OPENING);
        // Keep pre-maintenance state handler but allow doors to open for passenger exit
        System.out.println("Pre-maintenance elevator " + elevator.getId() + " opening doors for passenger exit");
    }
    
    @Override
    public void closeDoors(Elevator elevator) {
        elevator.setState(ElevatorState.DOORS_CLOSING);
        System.out.println("Pre-maintenance elevator " + elevator.getId() + " closing doors");
    }
    
    @Override
    public void enterMaintenance(Elevator elevator) {
        // Already in pre-maintenance, transition to full maintenance
        elevator.setState(ElevatorState.MAINTENANCE);
        elevator.setStateHandler(new MaintenanceState());
        elevator.setActive(false);
        System.out.println("Elevator " + elevator.getId() + " transitioning from pre-maintenance to full maintenance");
    }
    
    @Override
    public void exitMaintenance(Elevator elevator) {
        // Exit pre-maintenance and return to normal operation
        elevator.setState(ElevatorState.STOPPED);
        elevator.setStateHandler(new StoppedState());
        elevator.setActive(true);
        System.out.println("Elevator " + elevator.getId() + " exiting pre-maintenance mode");
    }
    
    @Override
    public boolean canAcceptExternalRequests(Elevator elevator) {
        // Block new external requests in pre-maintenance
        return false;
    }
    
    @Override
    public boolean canAcceptInternalRequests(Elevator elevator) {
        // Block new internal requests in pre-maintenance
        return false;
    }
    
    @Override
    public String getStateName() {
        return "PRE_MAINTENANCE";
    }
}

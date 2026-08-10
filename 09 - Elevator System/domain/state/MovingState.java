package domain.state;

import domain.Elevator;

/**
 * Concrete state for when elevator is moving
 */
public class MovingState implements ElevatorStateHandler {
    
    
    @Override
    public void openDoors(Elevator elevator) {
        // Cannot open doors while moving
        System.out.println("Cannot open doors while elevator " + elevator.getId() + " is moving");
    }
    
    @Override
    public void closeDoors(Elevator elevator) {
        // Doors should already be closed while moving
        System.out.println("Elevator " + elevator.getId() + " doors already closed while moving");
    }
    
    @Override
    public void enterMaintenance(Elevator elevator) {
        // Transition to pre-maintenance to complete current movement
        elevator.setStateHandler(new PreMaintenanceState());
        System.out.println("Elevator " + elevator.getId() + " entering pre-maintenance mode - will complete current movement");
    }
    
    @Override
    public void exitMaintenance(Elevator elevator) {
        // Not in maintenance, no action needed
        System.out.println("Elevator " + elevator.getId() + " is not in maintenance mode");
    }
    
    @Override
    public boolean canAcceptExternalRequests(Elevator elevator) {
        return elevator.isActive() && !elevator.isFull();
    }
    
    @Override
    public boolean canAcceptInternalRequests(Elevator elevator) {
        return elevator.isActive();
    }
    
    @Override
    public String getStateName() {
        return "MOVING";
    }
}

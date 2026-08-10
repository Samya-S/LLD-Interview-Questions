package main;

import controller.ElevatorController;
import controller.FloorPanelController;
import controller.ElevatorPanelController;
import domain.Building;
import domain.Elevator;
import domain.strategy.LoadBalancingStrategy;

public class ElevatorSystemSimulation {
    
    public static void main(String[] args) {
        // Initialize controllers
        ElevatorController elevatorController = new ElevatorController();
        FloorPanelController floorPanelController = new FloorPanelController(elevatorController);
        ElevatorPanelController elevatorPanelController = new ElevatorPanelController(elevatorController);
        
        try {
            System.out.println("=== ELEVATOR SYSTEM SIMULATION ===");
            
            // 1. Create building
            Building building = elevatorController.getBuildingService()
                    .createBuilding("Tech Tower", 1, 10, 3);
            String buildingId = building.getId();
            System.out.println("Created building: " + building.getName() + 
                              " (Floors: " + building.getMinFloor() + "-" + building.getMaxFloor() + ")");
            
            // 2. Create elevators
            Elevator elevator1 = elevatorController.createElevator(buildingId, 8);
            Elevator elevator2 = elevatorController.createElevator(buildingId, 8);
            Elevator elevator3 = elevatorController.createElevator(buildingId, 8);
            
            System.out.println("Created 3 elevators with capacity 8 each");
            System.out.println("Elevator IDs: " + elevator1.getId() + ", " + 
                              elevator2.getId() + ", " + elevator3.getId());
            
            // 3. Set strategies
            elevatorController.getDispatcherService()
                    .setElevatorSelectionStrategy(new LoadBalancingStrategy());
            System.out.println("Set elevator selection strategy to Load Balancing");
            
            // 4. Start elevator system
            elevatorController.startElevatorSystem(buildingId);
            Thread.sleep(1000);
            
            // 5. Simulate external requests (floor panel presses)
            System.out.println("\n=== SIMULATING EXTERNAL REQUESTS ===");
            floorPanelController.pressUpButton(3, buildingId);
            floorPanelController.pressUpButton(7, buildingId);
            floorPanelController.pressDownButton(9, buildingId);
            floorPanelController.pressUpButton(2, buildingId);
            
            Thread.sleep(3000);
            
            // 6. Simulate internal requests (elevator panel selections)
            System.out.println("\n=== SIMULATING INTERNAL REQUESTS ===");
            elevatorPanelController.selectFloor(elevator1.getId(), 5);
            elevatorPanelController.selectFloor(elevator1.getId(), 8);
            elevatorPanelController.selectFloor(elevator2.getId(), 4);
            elevatorPanelController.selectFloor(elevator3.getId(), 6);
            
            Thread.sleep(5000);
            
            
            // Try to add requests during pre-maintenance (should be rejected)
            elevatorPanelController.selectFloor(elevator1.getId(), 9);
            floorPanelController.pressUpButton(4, buildingId);
            elevatorPanelController.selectFloor(elevator2.getId(), 10);
            
            Thread.sleep(3000);
            
            // 8. Test full maintenance mode
            System.out.println("\n=== TESTING FULL MAINTENANCE MODE ===");
            elevatorController.setElevatorMaintenance(elevator2.getId(), true);
            
            // Try to add requests during full maintenance (should be rejected)
            elevatorPanelController.selectFloor(elevator2.getId(), 7);
            
            Thread.sleep(2000);
            
            
            // Try to add request during system maintenance
            floorPanelController.pressUpButton(6, buildingId);
            
            Thread.sleep(3000);
            
            // 10. Exit maintenance mode for one elevator
            System.out.println("\n=== EXITING MAINTENANCE MODE ===");
            elevatorController.setElevatorMaintenance(elevator2.getId(), false);
            
            Thread.sleep(1000);
            
            // 11. Stop system gracefully
            System.out.println("\n=== STOPPING SYSTEM GRACEFULLY ===");
            elevatorController.stopElevatorSystem(buildingId);
            
            // Try to add request after system stop
            floorPanelController.pressUpButton(8, buildingId);
            
            System.out.println("\n=== SIMULATION COMPLETED ===");
            
        } catch (Exception e) {
            System.err.println("Error during simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

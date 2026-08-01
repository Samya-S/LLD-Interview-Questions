
// Domain imports
import domain.Direction;

// Repository imports
import repository.IntersectionRepository;
import repository.EmergencyRepository;
import repository.TrafficRepository;
import repository.TimingRepository;

// Service imports
import service.IntersectionService;
import service.EmergencyService;
import service.TrafficService;
import service.TimingService;

// Controller imports
import controller.IntersectionController;
import controller.EmergencyController;
import controller.TrafficController;
import controller.TimingController;

public class TrafficSignalSimulation {
    public static void main(String[] args) {
        System.out.println("=== Traffic Signal System Simulation ===");

        // Initialize repositories
        IntersectionRepository intersectionRepository = new IntersectionRepository();
        EmergencyRepository emergencyRepository = new EmergencyRepository();
        TrafficRepository trafficRepository = new TrafficRepository();
        TimingRepository timingRepository = new TimingRepository();

        // Initialize services
        IntersectionService intersectionService = new IntersectionService(intersectionRepository, timingRepository);
        EmergencyService emergencyService = new EmergencyService(emergencyRepository, intersectionService);
        TrafficService trafficService = new TrafficService(trafficRepository);
        TimingService timingService = new TimingService(timingRepository, trafficService);

        // Initialize controllers
        IntersectionController intersectionController = new IntersectionController(intersectionService);
        EmergencyController emergencyController = new EmergencyController(emergencyService);
        TrafficController trafficController = new TrafficController(trafficService);
        TimingController timingController = new TimingController(timingService);

        System.out.println("\n--- Creating Intersection ---");
        intersectionController.createIntersection(1, "Main Street & Oak Avenue");

        System.out.println("\n--- Setting Signal Timings ---");
        timingController.setSignalTiming(1, Direction.NORTH, 30);
        timingController.setSignalTiming(1, Direction.SOUTH, 30);
        timingController.setSignalTiming(1, Direction.EAST, 15);
        timingController.setSignalTiming(1, Direction.WEST, 15);

        System.out.println("\n--- Enabling Dynamic Timing ---");
        timingController.enableDynamicTiming(1, Direction.NORTH, true);
        timingController.enableDynamicTiming(1, Direction.SOUTH, true);

        System.out.println("\n--- Starting Automatic Cycle ---");
        intersectionController.startCycle(1);

        System.out.println("\n--- Displaying Initial Status ---");
        intersectionController.displayStatus(1);
        timingController.displayTimingStatus(1);

        System.out.println("\n--- Updating Traffic Counts ---");
        trafficController.updateVehicleCount(Direction.NORTH, 15);
        trafficController.updateVehicleCount(Direction.SOUTH, 8);
        trafficController.updateVehicleCount(Direction.EAST, 3);
        trafficController.updateVehicleCount(Direction.WEST, 12);

        System.out.println("\n--- Displaying Traffic Status ---");
        trafficController.displayTrafficStatus();

        System.out.println("\n--- Adjusting Timing Based on Traffic ---");
        timingController.adjustTimingBasedOnTraffic(1, Direction.NORTH);
        timingController.adjustTimingBasedOnTraffic(1, Direction.SOUTH);

        System.out.println("\n--- Requesting Emergency ---");
        emergencyController.requestEmergency(1, Direction.EAST, 30);

        System.out.println("\n--- Displaying Status During Emergency ---");
        intersectionController.displayStatus(1);
        emergencyController.getEmergencyStatus(1);

        System.out.println("\n--- Ending Emergency ---");
        emergencyController.endEmergency(1);

        System.out.println("\n--- Displaying Final Status ---");
        intersectionController.displayStatus(1);
        timingController.displayTimingStatus(1);

        System.out.println("\n=== Simulation Complete ===");
    }
}
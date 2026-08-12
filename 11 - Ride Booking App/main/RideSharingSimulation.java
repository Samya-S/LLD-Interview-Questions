package main;

import controller.DriverController;
import controller.PaymentController;
import controller.RideController;
import domain.Driver;
import domain.DriverStatus;
import domain.Location;
import domain.PaymentStatus;
import domain.PaymentType;
import domain.Ride;
import domain.RideRequest;
import domain.Rider;
import domain.RideStatusResponse;
import domain.strategy.BasePricingStrategy;
import domain.strategy.NearestDriverStrategy;
import repository.DriverRepository;
import repository.RideRepository;
import repository.RiderRepository;
import repository.LocationRepository;
import repository.impl.InMemoryDriverRepository;
import repository.impl.InMemoryRideRepository;
import repository.impl.InMemoryRiderRepository;
import repository.impl.InMemoryLocationRepository;
import service.DriverService;
import service.LockService;
import service.LocationService;
import service.MatchingService;
import service.PaymentService;
import service.PricingService;
import service.RideService;
import service.notification.NotificationService;

public class RideSharingSimulation {
    public static void main(String[] args) {
        RideRepository rideRepository = new InMemoryRideRepository();
        RiderRepository riderRepository = new InMemoryRiderRepository();
        DriverRepository driverRepository = new InMemoryDriverRepository();
        LocationRepository locationRepository = new InMemoryLocationRepository();

        Rider rider = new Rider("rider-1", "Alice Rider", "alice@example.com", "111-222-3333",
                System.currentTimeMillis());
        riderRepository.save(rider);

        Driver driver = new Driver("driver-1", "Bob Driver", "bob@example.com", "444-555-6666", "KA01AB1234", "Sedan",
                DriverStatus.ONLINE);
        driver.setCurrentLocation(new Location(37.7749, -122.4194, "Market St", System.currentTimeMillis()));
        driverRepository.save(driver);

        NotificationService notificationService = new NotificationService();
        PricingService pricingService = new PricingService(new BasePricingStrategy());
        LocationService locationService = new LocationService(locationRepository);
        LockService lockService = new LockService();
        MatchingService matchingService = new MatchingService(driverRepository, rideRepository,
                new NearestDriverStrategy(), lockService, notificationService);
        PaymentService paymentService = new PaymentService(rideRepository, notificationService);
        RideService rideService = new RideService(rideRepository, riderRepository, driverRepository, matchingService,
                pricingService, paymentService, notificationService, locationService, lockService);
        DriverService driverService = new DriverService(driverRepository);

        RideController rideController = new RideController(rideService);
        DriverController driverController = new DriverController(driverService, rideService);
        PaymentController paymentController = new PaymentController(rideService);

        Location pickup = new Location(37.7749, -122.4194, "Market St", System.currentTimeMillis());
        Location drop = new Location(37.7840, -122.4090, "Mission St", System.currentTimeMillis());
        RideRequest prePaymentRequest = new RideRequest(rider.getId(), pickup, drop, PaymentType.PRE_PAYMENT);

        System.out.println("--- Requesting ride with PRE_PAYMENT ---");
        Ride ride = rideController.requestRide(prePaymentRequest);
        System.out.println("Ride created with id " + ride.getId() + " paymentId=" + ride.getPaymentId());

        paymentController.handleCallback(ride.getPaymentId(), PaymentStatus.COMPLETED);
        driverController.acceptRide(ride.getId(), driver.getId());
        driverController.startRide(ride.getId(), driver.getId());
        driverController.completeRide(ride.getId(), driver.getId());
        RideStatusResponse completedStatus = rideController.getRideStatus(ride.getId());
        System.out
                .println("Ride status: " + completedStatus.getStatus() + " fare=" + completedStatus.getEstimatedFare());

        System.out.println("--- Requesting ride with POST_PAYMENT (cash) ---");
        RideRequest postPaymentRequest = new RideRequest(rider.getId(), pickup, drop, PaymentType.POST_PAYMENT);
        Ride cashRide = rideController.requestRide(postPaymentRequest);
        driverController.acceptRide(cashRide.getId(), driver.getId());
        driverController.startRide(cashRide.getId(), driver.getId());
        driverController.completeRide(cashRide.getId(), driver.getId());
        RideStatusResponse cashStatus = rideController.getRideStatus(cashRide.getId());
        System.out.println("Cash ride status: " + cashStatus.getStatus() + " fare=" + cashStatus.getEstimatedFare());
    }
}

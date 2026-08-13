# RIDE BOOKING APP (UBER/GRAB) LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. As a rider, I can register and log in.
2. As a driver, I can register/onboard and go online/offline.
3. As a rider, I can set pickup and destination via map/search.
4. As a rider, I can see an upfront fare estimate with ETA before requesting.
5. As a rider, I can request a ride based on the fare estimate.
6. As a rider, I can cancel a ride before pickup (subject to cancellation policy).
7. The system asynchronously matches me to the nearest available driver within a bounded search radius.
8. The system ensures no driver is double-assigned to multiple active rides.
9. As a driver, I can accept or decline a ride request within a timeout window.
10. As a driver, I can navigate to pickup location and then to drop-off location.
11. As a driver, I can start and complete a trip.
12. As a rider, I can track the driver's location and trip progress in real time via GPS.
13. The driver app sends location updates every N seconds to the system.
14. Payment is processed on trip completion using supported methods (card/wallet/cash).
15. As a rider, I receive a receipt after payment.
16. Cancellation fees are applied per policy rules.

### EDGE CASES:

1. Driver matching policy: nearest by distance vs fastest ETA? Search radius and accept-timeout?
2. Pricing model: how to calculate fare (base + distance + time)? Surge pricing needed?
3. Payment? Pre-authorization or post-charge?
4. Single pricing or price can change later after ride completion?
5. How does cancellation policy work on post payment trips?

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **Ride** (Core Entity)
   - id: int [PK]
   - rideId: String [UNIQUE]
   - riderId: int [FK to Rider]
   - driverId: int [FK to Driver, NULLABLE] (NULL until assigned)
   - pickupLocation: Location (latitude, longitude, address)
   - dropoffLocation: Location (latitude, longitude, address)
   - status: RideStatus (Enum: REQUESTED, ASSIGNED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED)
   - estimatedFare: long (Stored as integer: actual amount * 100, e.g., 25.50 = 2550)
   - estimatedDistance: double (in km)
   - actualDistance: double [NULLABLE] (in km, captured for analytics/ETA tuning only)
   - estimatedDuration: long (in seconds)
   - actualDuration: long [NULLABLE] (in seconds, captured for analytics/ETA tuning only)
   - requestedAt: LocalDateTime
   - assignedAt: LocalDateTime [NULLABLE]
   - acceptedAt: LocalDateTime [NULLABLE]
   - startedAt: LocalDateTime [NULLABLE]
   - completedAt: LocalDateTime [NULLABLE]
   - cancelledAt: LocalDateTime [NULLABLE]
   - cancellationReason: String [NULLABLE]
   - paymentType: PaymentType (Enum: PRE_PAYMENT, POST_PAYMENT)
   - paymentId: String [NULLABLE] (Payment gateway transaction ID)
   - paymentStatus: PaymentStatus (Enum: PENDING, COMPLETED, FAILED, REFUNDED)

2. **Rider**
   - id: int [PK]
   - username: String [UNIQUE]
   - email: String [UNIQUE]
   - phoneNumber: String [UNIQUE]
   - name: String
   - createdAt: LocalDateTime

3. **Driver**
   - id: int [PK]
   - username: String [UNIQUE]
   - email: String [UNIQUE]
   - phoneNumber: String [UNIQUE]
   - name: String
   - licenseNumber: String [UNIQUE]
   - vehicleNumber: String [UNIQUE]
   - vehicleType: String (Sedan, SUV, etc.)
   - isOnline: boolean (true when driver is available for rides)
   - currentLocation: Location [NULLABLE] (latitude, longitude - updated via GPS)
   - lastLocationUpdate: LocalDateTime [NULLABLE]
   - createdAt: LocalDateTime

4. **Location**
   - latitude: double
   - longitude: double
   - address: String [NULLABLE] (Geocoded address)
   - timestamp: LocalDateTime

5. **RideStatus** (Enum)
   - REQUESTED, ASSIGNED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED

6. **PaymentStatus** (Enum)
   - PENDING, COMPLETED, FAILED, REFUNDED

7. **PaymentType** (Enum)
   - PRE_PAYMENT, POST_PAYMENT

8. **DriverStatus** (Enum)
   - ONLINE, OFFLINE, ON_RIDE

### RESPONSE DTOs:

9. **RideStatusResponse** (Response DTO - Built from queries)
   - rideId: String
   - status: RideStatus
   - driver: DriverInfo [NULLABLE] (id, name, phoneNumber, vehicleNumber, currentLocation, eta)
   - currentLocation: Location [NULLABLE] (Driver's current location if in progress)
   - estimatedFare: long [NULLABLE]
   - pickupLocation: Location
   - dropoffLocation: Location
   - requestedAt: LocalDateTime
   - Note: This is a response object built on-the-fly for polling endpoint

10. **FareEstimateResponse** (Response DTO)
    - estimatedFare: long (Stored as integer: actual amount * 100)
    - estimatedDistance: double (in km)
    - estimatedDuration: long (in seconds)
    - currency: String (e.g., "USD")

### REQUEST DTOs:

11. **RideRequest**
    - riderId: int
    - pickupLocation: Location (latitude, longitude, address)
    - dropoffLocation: Location (latitude, longitude, address)
    - paymentType: PaymentType (Enum: PRE_PAYMENT, POST_PAYMENT)
    - Note: POST_PAYMENT is cash only (no payment gateway). PRE_PAYMENT uses payment gateway with callback.

12. **FareEstimateRequest**
    - pickupLocation: Location
    - dropoffLocation: Location

13. **LocationUpdateRequest**
    - driverId: int
    - location: Location (latitude, longitude)
    - timestamp: LocalDateTime

---

## STEP-3: VISUALIZE INTERACTION FLOWS

1. **Fare Estimate:**  
   GET /api/rides/fare-estimate -\> RideController.getFareEstimate(request) -\> PricingService.calculateFare(pickup, dropoff) -\>
   MapService.getDistanceAndDuration(pickup, dropoff) -\>
   PricingStrategy.calculateFare(distance, duration) -\> Return FareEstimateResponse

2. **Request Ride:**
   - Create Ride(status=REQUESTED, paymentType, estimatedFare) -\>
   - If paymentType is PRE_PAYMENT:
     - Initiate payment via PaymentService.initiatePayment(rideId, estimatedFare) -\> PaymentGatewayRouter.selectProvider(...) -\> provider.initiatePayment(...) -\>
     - Return paymentId/providerRef to client
     - Update Ride(paymentStatus=PENDING, paymentId) -\>
     - DO NOT start matching yet (wait for payment callback)
     - Return (rideId, status: REQUESTED, paymentStatus: PENDING) immediately
   - If paymentType is POST_PAYMENT:
     - Validate: only cash payment allowed (no payment gateway)
     - Start Async MatchingService.matchDriver(ride) immediately (cash payment after ride)
   - Return (rideId, status: REQUESTED) immediately (non-blocking)
   
   - Payment Callback (PRE_PAYMENT only):
     - POST /api/payments/callback -\> PaymentController.handlePaymentCallback(transactionId, status) -\>
     - PaymentService.handlePaymentCallback(transactionId, status) -\>
     - Verify callback authenticity -\>
     - Find ride by paymentId -\>
     - If payment SUCCESS: Update Ride(paymentStatus=COMPLETED) -\> 
       - Start Async MatchingService.matchDriver(ride) -\> Push notification to rider
     - If payment FAILS: Update Ride(status=CANCELLED, paymentStatus=FAILED) -\> 
       - Push notification to rider (do not start matching)

   - Async Matching Flow:
     - MatchingService.matchDriver(ride) -\>
     - Fetch ride; ensure status is still REQUESTED
     - Find all available drivers: DriverRepository.findByStatus(DriverStatus.ONLINE)
     - Apply MatchingStrategy.findMatchingDrivers(pickup, availableDrivers, maxResults=3) to get top N candidates sorted by distance
     - For each driver candidate (in order):
       - Acquire distributed lock on driver (lockKey="driver_lock_{driverId}", timeout=200ms)
       - Re-fetch driver to get latest status
       - Validate driver is still ONLINE
       - Keep driver status as ONLINE (driver will be set to ON_RIDE only when they accept)
       - Push notification to driver with ride details (do NOT assign driver yet)
       - Wait for driver response (poll ride status every 500ms, timeout 30 seconds):
         - If status becomes ACCEPTED and driverId matches: driver accepted, return driver and break loop
         - If status becomes CANCELLED: ride cancelled, return empty
         - If another driver was assigned: break and continue to next driver
         - If timeout: driver didn't respond, continue to next driver (no assignment was made, so nothing to reset)
       - Release lock when done
     - If no drivers accepted: return empty (no driver matched)

3. **Driver Accept/Decline (Push Notification Triggered):**
   - Driver receives push notification -\> Opens app -\>
   - POST /api/rides/{rideId}/accept -\> RideController.acceptRide(rideId, driverId) -\>
   - RideService.driverAccept(rideId, driverId) -\>
   - Acquire lock on ride (lockKey="ride_lock_{rideId}", timeout=500ms) -\>
   - Fetch ride -\>
   - If ride status is REQUESTED: assign driver first (driverId=driverId, status=ASSIGNED, assignedAt=now) -\>
   - Validate driverId matches ride's driverId -\>
   - Set driver status to ON_RIDE (driver was ONLINE during matching, now confirmed) -\>
   - Update Ride(status=ACCEPTED, acceptedAt=now) -\>
   - Transition ride state via State Pattern -\>
   - Push notification to rider -\>
   - Release lock
   - (MatchingService wait loop detects ACCEPTED status and returns driver)
   
   OR
   
   - POST /api/rides/{rideId}/decline -\> RideController.declineRide(rideId, driverId) -\>
   - RideService.driverDecline(rideId, driverId) -\>
   - Acquire lock on ride -\>
   - Fetch ride -\>
   - If ride status is REQUESTED: driver declining during matching (no assignment made yet), just return -\>
   - If ride status is ASSIGNED: validate driverId matches -\>
     - Release driver (set status back to ONLINE) -\>
     - Update Ride(driverId=null, status=REQUESTED) -\>
     - Trigger re-matching: MatchingService.matchDriver(ride) -\>
   - Release lock
   - (MatchingService wait loop continues to next driver automatically)

4. **Rider Polling for Status:**  
   GET /api/rides/{rideId}/status -\> RideController.getRideStatus(rideId) -\> RideService.getRideStatus(rideId) -\>
   Build RideStatusResponse with current state -\> Return (rider polls every 2-3 seconds)

5. **Driver Location Updates (GPS):**  
   POST /api/drivers/{driverId}/location -\> DriverController.updateLocation(request) -\>
   LocationService.updateDriverLocation(driverId, location) -\>
   Update Driver.currentLocation and lastLocationUpdate -\>
   If driver has active ride, notify rider via push notification

6. **Start Trip:**  
   POST /api/rides/{rideId}/start -\> RideController.startRide(rideId, driverId) -\>
   RideService.startRide(rideId, driverId) -\>
   Validate ride is ACCEPTED -\> Update Ride(status=IN_PROGRESS, startedAt=now) -\>
   Push notification to rider -\> Start real-time location tracking

7. **Complete Trip:**  
   POST /api/rides/{rideId}/complete -\> RideController.completeRide(rideId, driverId) -\>
   RideService.completeRide(rideId, driverId) -\>
   Validate ride is IN_PROGRESS -\>
   Capture actual distance and duration for telemetry (does not affect fare) -\>
   Update Ride(status=COMPLETED, completedAt=now, actualDistance, actualDuration) -\>
   If paymentType is POST_PAYMENT: driver collects the locked estimatedFare in cash -\> Update Ride(paymentStatus=COMPLETED) -\>
   If paymentType is PRE_PAYMENT: payment already settled during request/callback; simply confirm completion and send receipt -\>
   Push notification to rider -\> Release driver for new rides

8. **Payment Callback:**  
   POST /api/payments/callback -\> PaymentController.handlePaymentCallback(transactionId, status) -\>
   PaymentService.handlePaymentCallback(transactionId, status) -\>
   Verify callback authenticity -\>
   On SUCCESS: Update Ride(paymentStatus=COMPLETED) -\> Push receipt to rider
   On FAILURE: Update Ride(paymentStatus=FAILED) -\> Retry or notify rider

9. **Cancel Ride:**  
   POST /api/rides/{rideId}/cancel -\> RideController.cancelRide(rideId, userId, reason) -\>
   RideService.cancelRide(rideId, userId, reason) -\>
   Validate cancellation policy -\>
   Update Ride(status=CANCELLED, cancelledAt=now, cancellationReason) -\>
   If driver assigned: release driver, push notification -\>
   Apply cancellation fee if applicable -\> Push notification to rider/driver

10. **Driver Go Online/Offline:**  
    POST /api/drivers/{driverId}/online -\> DriverController.goOnline(driverId) -\>
    DriverService.goOnline(driverId) -\> Update Driver(isOnline=true) -\>
    Register driver for push notifications
    
    POST /api/drivers/{driverId}/offline -\> DriverController.goOffline(driverId) -\>
    DriverService.goOffline(driverId) -\> Update Driver(isOnline=false) -\>
    Unregister from push notifications

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **RideController**
   - RideStatusResponse getRideStatus(String rideId)
   - Ride requestRide(RideRequest request)
   - void cancelRide(String rideId, String reason)
   - FareEstimateResponse getFareEstimate(FareEstimateRequest request)

2. **DriverController**
   - void acceptRide(String rideId, String driverId)
   - void declineRide(String rideId, String driverId)
   - void startRide(String rideId, String driverId)
   - void completeRide(String rideId, String driverId)
   - void updateLocation(String driverId, Location location)
   - void goOnline(String driverId)
   - void goOffline(String driverId)

3. **PaymentController**
   - void handlePaymentCallback(String transactionId, PaymentStatus status)

### SERVICES:

1. **RideService**
   - Ride requestRide(RideRequest request)
   - RideStatusResponse getRideStatus(String rideId)
   - void cancelRide(String rideId, String reason)
   - void driverAccept(String rideId, String driverId)
   - void driverDecline(String rideId, String driverId)
   - void startRide(String rideId, String driverId)
   - void completeRide(String rideId, String driverId)

2. **MatchingService** (Async)
   - Optional\<Driver\> matchDriver(Ride ride)
   - void releaseDriver(String driverId)

3. **PricingService**
   - FareEstimateResponse calculateFare(Location pickup, Location dropoff)

4. **PaymentService**
   - String initiatePayment(String rideId, long amount) (PRE_PAYMENT only)
   - void handlePaymentCallback(String transactionId, PaymentStatus status) (PRE_PAYMENT only)

5. **LocationService**
   - void updateDriverLocation(int driverId, Location location)
   - Location getDriverLocation(int driverId)
   - double calculateDistance(Location loc1, Location loc2)
   - long calculateETA(Location from, Location to)

6. **DriverService**
   - void goOnline(int driverId)
   - void goOffline(int driverId)
   - Driver getById(int driverId)
   - boolean isAvailable(int driverId)

7. **LockService** (Distributed)
   - boolean acquire(String key, long timeoutMs)
   - void release(String key)

8. **NotificationService** (Push Notifications)
   - void sendToDriver(int driverId, NotificationMessage message)
   - void sendToRider(int riderId, NotificationMessage message)

9. **MapService** (External Integration)
   - DistanceAndDuration getDistanceAndDuration(Location from, Location to)
   - String geocode(Location location)
   - Location reverseGeocode(double lat, double lon)

### REPOSITORIES:

1. **RideRepository**
   - Optional\<Ride\> findById(int id)
   - Optional\<Ride\> findByRideId(String rideId)
   - Ride save(Ride ride)
   - List\<Ride\> findByRiderId(int riderId)
   - List\<Ride\> findByDriverId(int driverId)
   - List\<Ride\> findByStatus(RideStatus status)

2. **RiderRepository**
   - Optional\<Rider\> findById(int id)
   - Optional\<Rider\> findByEmail(String email)
   - Rider save(Rider rider)

3. **DriverRepository**
   - Optional\<Driver\> findById(String id)
   - Optional\<Driver\> findByEmail(String email)
   - List\<Driver\> findByStatus(DriverStatus status) (returns all drivers with given status, e.g., ONLINE)
   - Driver save(Driver driver)
   - void updateLocation(String driverId, Location location)

4. **LocationRepository**
   - void saveLocation(int driverId, Location location)
   - Location getLatestLocation(int driverId)

### RIDE STATE PATTERN (State Machine):
- RideState (Interface)
  + void accept(Ride ride, int driverId)
  + void cancel(Ride ride, int userId, String reason)
  + void start(Ride ride, int driverId)
  + void complete(Ride ride, int driverId)
- RequestedState (Concrete)
- AssignedState (Concrete)
- AcceptedState (Concrete)
- InProgressState (Concrete)
- CompletedState (Concrete)
- CancelledState (Concrete)
Note: State objects are created on-demand from Ride.status enum (not stored in entity).
The status enum is persisted in database; state objects provide behavior encapsulation.

### DRIVER MATCHING STRATEGY:
- DriverMatchingStrategy (Strategy Interface)
  + List\<Driver\> findMatchingDrivers(Location pickup, List\<Driver\> candidates, int maxResults)
- NearestDriverStrategy (Concrete - finds nearest by straight-line distance, sorts all candidates and returns top N)
- FastestEtaStrategy (Concrete - finds fastest ETA using routing, can be added later)
- MatchingService (Context)
  - DriverMatchingStrategy matchingStrategy
  + Optional\<Driver\> matchDriver(Ride ride)
Note: Strategy receives all ONLINE drivers as candidates, no explicit radius filtering at repository level

### PRICING STRATEGY:
- PricingStrategy (Strategy Interface)
  + long calculateFare(double distance, long duration, PricingContext context)
- BasePricingStrategy (Concrete - base + distance + time)
- SurgePricingStrategy (Concrete - applies surge multiplier)
- PricingService (Context)
  - PricingStrategy strategy
  + void setStrategy(PricingStrategy strategy)
  + FareEstimateResponse calculateFare(Location pickup, Location dropoff)

### PAYMENT GATEWAY STRATEGY (External Providers):
- PaymentGatewayProvider (Strategy Interface)
  + String getName()
  + String initiatePayment(String rideId, long amount, Map\<String, String\> paymentDetails)
  + boolean verifyCallback(String transactionId, PaymentStatus status)
- StripePaymentGatewayProvider (Concrete)
- RazorpayPaymentGatewayProvider (Concrete)
- PayPalPaymentGatewayProvider (Concrete)
- MockPaymentGatewayProvider (Concrete - simulation)
- PaymentGatewayRouter (Context/Router)
  - Map\<String, PaymentGatewayProvider\> providers
  + PaymentGatewayProvider selectProvider(String preferredGateway, long amount)
  + PaymentGatewayProvider resolve(String gatewayName)

---

## STEP-5: CORE USE CASES AND METHODS

---

## STEP-6: DESIGN PATTERNS AND OOP PRINCIPLES

### DESIGN PATTERNS USED:
1. Repository Pattern - data access abstraction for Ride/Driver/Rider/Location
2. Service Layer - business logic separation (RideService/MatchingService/PricingService/PaymentService)
3. State Pattern - Ride lifecycle management (RequestedState, AssignedState, AcceptedState, InProgressState, CompletedState, CancelledState)
4. Strategy Pattern - DriverMatchingStrategy (NearestDistanceStrategy, FastestEtaStrategy)
5. Strategy Pattern - PricingStrategy (BasePricingStrategy, SurgePricingStrategy)
6. Strategy Pattern - PaymentGatewayProvider + Router (Stripe, Razorpay, PayPal, Mock)
7. Observer Pattern (lightweight) - location updates trigger notifications to riders
8. RESTful API Design - clean resource-oriented endpoints

### OOP PRINCIPLES APPLIED:
1. Single Responsibility - each service focuses on one concern (RideService manages rides, MatchingService handles matching, etc.)
2. Open/Closed - add new matching strategies, pricing strategies, or payment gateways without modifying core logic
3. Encapsulation - ride state transitions only through State Pattern; location updates only through LocationService
4. Dependency Inversion - services depend on interfaces (repositories, strategies, payment gateways, map service)
5. Idempotency - payment callbacks and ride actions can be safely retried without duplicate processing

### KEY RELATIONSHIPS:
- Association | uses: Ride references Rider via riderId
- Association | uses: Ride references Driver via driverId (nullable until assigned)
- Association | manages: Ride has pickupLocation and dropoffLocation (Location objects)
- Dependency | uses: Services depend on repositories, LockService, PaymentGatewayService, MapService
- Association | uses: Driver has currentLocation (Location object)

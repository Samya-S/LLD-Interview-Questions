# ELEVATOR SYSTEM LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. Building should support multiple floors and multiple elevators
2. Each floor should have external panels with up/down buttons
3. Each elevator should have internal panels with floor selection, open, close buttons
4. System should dispatch elevators efficiently to minimize response time
5. Elevators should move based on dynamic strategies (FCFS, SCAN)
6. People can enter/exit only when elevator is stopped with doors open
7. System should support adding/removing elevators dynamically
8. System should maintain building constraints (top floor, bottom floor)

### EDGE CASES:

1. What happens when elevator reaches capacity? What is the capacity?
2. How to handle emergency situations?
3. What if multiple elevators are equidistant from a request?
4. How to handle elevator maintenance mode?

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **Building**
   - id: String [PK]
   - name: String
   - minFloor: int
   - maxFloor: int
   - totalElevators: int
   - systemState: SystemState (RUNNING, STOPPING, STOPPED, MAINTENANCE)

2. **Elevator**
   - id: String [PK]
   - buildingId: String [FK]
   - currentFloor: int
   - direction: Direction (UP, DOWN, IDLE)
   - capacity: int
   - currentLoad: int
   - isActive: boolean
   - stateHandler: ElevatorStateHandler (Holds the current state)

3. **ExternalRequest**
   - id: String [PK]
   - floorNumber: int
   - buildingId: String [FK]
   - direction: Direction (UP, DOWN)
   - timestamp: long
   - status: RequestStatus (PENDING, ASSIGNED, COMPLETED, QUEUED)
   - assignedElevatorId: String [FK] (nullable)

4. **InternalRequest**
   - id: String [PK]
   - elevatorId: String [FK]
   - destinationFloor: int
   - timestamp: long
   - status: RequestStatus (PENDING, COMPLETED)

5. **Direction** (Enum)
   - UP, DOWN, IDLE

6. **ElevatorState** (Enum)
   - MOVING, STOPPED, DOORS_OPENING, DOORS_CLOSING, MAINTENANCE

7. **RequestStatus** (Enum)
   - PENDING, ASSIGNED, COMPLETED, QUEUED

8. **SystemState** (Enum)
   - RUNNING, STOPPING, STOPPED, MAINTENANCE

---

## STEP-3: VISUALIZE INTERACTION FLOWS

1. **Elevator Operations Flow:**
   - Create new elevator in building with given capacity
   - Move elevator to target floor when needed
   - Set elevator to maintenance mode when required
   - Admin starts elevator system
   - Admin stops elevator system

2. **Floor Panel Flow (External Requests):**
   - User presses up button on floor panel
   - User presses down button on floor panel

3. **Elevator Panel Flow (Internal Requests):**
   - User inside elevator selects destination floor

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **ElevatorController** (Elevator Operations)
   - Elevator createElevator(String buildingId, int capacity)
   - void moveElevator(String elevatorId, int targetFloor) // specific request
   - void setElevatorMaintenance(String elevatorId, boolean maintenance)
   - void startElevatorSystem(String buildingId)
   - void stopElevatorSystem(String buildingId)

2. **FloorPanelController** (External Requests)
   - void pressUpButton(int floorNumber, String buildingId)
   - void pressDownButton(int floorNumber, String buildingId)

3. **ElevatorPanelController** (Internal Requests)
   - void selectFloor(String elevatorId, int destinationFloor)

### SERVICES:

1. **ElevatorService** (Elevator Management)
   - Elevator createElevator(String buildingId, int capacity)
   - void updateElevatorState(String elevatorId, ElevatorState state)
   - void updateElevatorFloor(String elevatorId, int floor)
   - List\<Elevator\> getAvailableElevators(String buildingId)
   - Elevator findById(String elevatorId)
   - List\<Elevator\> findByBuilding(String buildingId)
   - void setRequestService(RequestService requestService) // Dependency injection

2. **RequestService** (Request Processing)
   - ExternalRequest createExternalRequest(int floor, Direction direction, String buildingId)
   - InternalRequest createInternalRequest(String elevatorId, int destinationFloor)
   - void completeRequest(String requestId)

3. **DispatcherService** (Elevator Selection & Request Queuing)
   - void queueExternalRequest(ExternalRequest request)
   - Elevator selectBestElevator(ExternalRequest request, List\<Elevator\> availableElevators)
   - void assignRequestToElevator(ExternalRequest request, Elevator elevator)
   - void processPendingRequests(String buildingId)
   - void setElevatorSelectionStrategy(ElevatorSelectionStrategy strategy)

4. **MovementService** (Elevator Movement & Scheduling)
   - void processElevatorMovement(String elevatorId, Elevator elevator)
   - void processAllElevatorMovements(String buildingId)
   - List\<Integer\> calculateStops(Elevator elevator, List\<InternalRequest\> requests)
   - void calculateStops(String elevatorId)
   - void setMovementStrategy(MovementStrategy strategy)
   - void startElevatorSystem(String buildingId) // Initializes ScheduledExecutorService
   - void stopElevatorSystem(String buildingId) // Gracefully shuts down scheduler
   - boolean hasPendingRequests(String buildingId) // Helper for graceful shutdown

5. **BuildingService** (Building Management & System Control)
   - Building createBuilding(String name, int minFloor, int maxFloor)
   - boolean isValidFloor(String buildingId, int floor)
   - void setBuildingSystemState(String buildingId, SystemState state)
   - boolean isSystemRunning(String buildingId)
   - Building findById(String buildingId)
   - boolean buildingExists(String buildingId)

### REPOSITORIES:

1. **ElevatorRepository**
   - Elevator save(Elevator elevator)
   - Optional\<Elevator\> findById(String elevatorId)
   - List\<Elevator\> findByBuilding(String buildingId)
   - List\<Elevator\> findAvailableElevators(String buildingId)
   - void deleteById(String elevatorId)

2. **ExternalRequestRepository**
   - ExternalRequest save(ExternalRequest request)
   - List\<ExternalRequest\> findPendingRequests(String buildingId)
   - List\<ExternalRequest\> findQueuedRequests(String buildingId)
   - void updateRequestStatus(String requestId, RequestStatus status)
   - Optional\<ExternalRequest\> findById(String requestId)
   - List\<ExternalRequest\> findAll()
   - void deleteById(String requestId)

3. **InternalRequestRepository**
   - InternalRequest save(InternalRequest request)
   - List\<InternalRequest\> findByElevator(String elevatorId)
   - List\<InternalRequest\> findPendingByElevator(String elevatorId)
   - Optional\<InternalRequest\> findById(String requestId)
   - List\<InternalRequest\> findAll()
   - void updateRequestStatus(String requestId, RequestStatus status)

4. **BuildingRepository**
   - Building save(Building building)
   - Optional\<Building\> findById(String buildingId)
   - List\<Building\> findAll()
   - void deleteById(String buildingId)

### STRATEGY PATTERN IMPLEMENTATION:

1. **ElevatorSelectionStrategy** (Strategy Interface)
   - Elevator selectElevator(ExternalRequest request, List\<Elevator\> elevators)

2. **NearestElevatorStrategy** (Concrete Strategy)
   - Elevator selectElevator() // Select closest available elevator

3. **LoadBalancingStrategy** (Concrete Strategy)
   - Elevator selectElevator() // Select elevator with least load

4. **MovementStrategy** (Strategy Interface)
   - List\<Integer\> calculatePath(Elevator elevator, List\<InternalRequest\> requests)

5. **ScanStrategy** (Concrete Strategy)
   - List\<Integer\> calculatePath() // SCAN algorithm - continue in current direction

6. **FCFSStrategy** (Concrete Strategy)
   - List\<Integer\> calculatePath() // First Come First Serve

### STATE PATTERN IMPLEMENTATION:

1. **ElevatorStateHandler** (State Interface)
   - void openDoors(Elevator elevator)
   - void closeDoors(Elevator elevator)
   - void enterMaintenance(Elevator elevator)
   - void exitMaintenance(Elevator elevator)
   - boolean canAcceptExternalRequests(Elevator elevator)
   - boolean canAcceptInternalRequests(Elevator elevator)
   - String getStateName()

2. **MovingState** (Concrete State)
   - void openDoors() // Cannot open doors while moving
   - void closeDoors() // Already closed while moving
   - void enterMaintenance() // Transition to PreMaintenanceState

3. **StoppedState** (Concrete State)
   - void openDoors() // Change to DoorsOpeningState
   - void closeDoors() // Already closed
   - void enterMaintenance() // Change to MaintenanceState

4. **DoorsOpeningState** (Concrete State)
   - void openDoors() // Already opening
   - void closeDoors() // Change to DoorsClosingState

5. **DoorsClosingState** (Concrete State)
   - void openDoors() // Change to DoorsOpeningState
   - void closeDoors() // Already closing

6. **MaintenanceState** (Concrete State)
   - void openDoors() // Allow for maintenance access
   - void closeDoors() // Allow for maintenance access
   - void exitMaintenance() // Change back to StoppedState

7. **PreMaintenanceState**
   - void enterMaintenance() // Already transitioning to maintenance
   - void exitMaintenance() // Cancel maintenance transition

---

## STEP-5: CORE USE CASES AND METHODS

---

## STEP-6: OOPS PRINCIPLES AND DESIGN PATTERNS USED

### DESIGN PATTERNS USED:

1. **Strategy Pattern** - for elevator selection and movement algorithms
2. **State Pattern** - for elevator door and maintenance states
3. **Repository Pattern** - for data access abstraction
4. **Service Layer Pattern** - for business logic separation
5. **Scheduler Pattern** - for periodic background processing using ScheduledExecutorService

### OOP PRINCIPLES APPLIED:

1. **Single Responsibility** - each service handles specific elevator operations
2. **Open/Closed** - easy to add new selection/movement strategies
3. **Encapsulation** - elevator state and behavior encapsulated in domain objects
4. **Dependency Inversion** - services depend on strategy interfaces, not implementations
5. **Polymorphism** - different strategies can be used interchangeably

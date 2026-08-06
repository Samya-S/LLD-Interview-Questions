# VENDING MACHINE LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. The system should support multiple products with different prices and quantities
2. The system should accept notes of different denominations ($1, $5, $10, $20, $50, $100)
3. The system should dispense selected products and return change if necessary
4. The system should track available products and quantities (inventory management)
5. The system should handle multiple transactions concurrently and ensure data consistency
6. The system should provide admin interface for restocking products and collecting money
7. The system should handle exceptional scenarios (insufficient funds, out-of-stock products)
8. The system should ensure operations are only allowed in appropriate states
9. The system should manage the following operational states:
   - IDLE: Ready to accept new transactions, users can browse and select products
   - PROCESSING_PAYMENT: User has selected product and is paying, can insert money or cancel
   - DISPENSING: Payment complete, machine is dispensing product and change
   - OUT_OF_SERVICE: Machine is not operational due to maintenance or malfunction

### EDGE CASES:

- Concurrent access to same product
- Insufficient change in machine
- Power failure?
- Product out of stock during transaction
- Payment failure scenarios

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **VendingMachine (Core Entity)**
   - id: int [PK]
   - location: String
   - currentState: VendingMachineState
   - isOperational: boolean

2. **Product**
   - id: int [PK]
   - name: String
   - price: Double
   - category: ProductCategory (Enum: BEVERAGE, SNACK, CANDY, etc.)

3. **Inventory**
   - productId: int [FK]
   - vendingMachineId: int [FK]
   - quantity: int
   - minThreshold: int (for restocking alerts)

4. **CashBox**
   - id: int [PK]
   - vendingMachineId: int [FK]
   - denominations: Map\<Denomination, Integer\> (denomination -> count)
   - totalAmount: Double

5. **Denomination (Enum)**
   - ONE_DOLLAR(100), FIVE_DOLLAR(500), TEN_DOLLAR(1000), TWENTY_DOLLAR(2000), FIFTY_DOLLAR(5000), HUNDRED_DOLLAR(10000)

6. **Transaction**
   - id: int [PK]
   - vendingMachineId: int [FK]
   - productId: int [FK]
   - amountInserted: Double
   - amountRequired: Double
   - changeReturned: Double
   - status: TransactionStatus (Enum: PENDING, COMPLETED, FAILED, CANCELLED)
   - timestamp: long

7. **TransactionStatus (Enum)**
   - PENDING, COMPLETED, FAILED, CANCELLED

8. **ProductCategory (Enum)**
   - BEVERAGE, SNACK, CANDY, CHIPS, COOKIES, OTHER

9. **Recovery (Power Failure Recovery)**
   - id: int [PK]
   - vendingMachineId: int [FK]
   - transactionId: int [FK]
   - state: VendingMachineState (PROCESSING_PAYMENT or DISPENSING)
   - status: RecoveryStatus (Enum: PENDING, COMPLETED)
   - createdAt: Long
   - completedAt: Long (nullable)

10. **RecoveryStatus (Enum)**
    - PENDING, COMPLETED

---

## STEP-3: VISUALIZE INTERACTION FLOWS

1. **Product Listing & Inventory Flow:**  
   GET /api/products -> System fetches all products -> System checks inventory levels ->
   System returns product list with availability status (in stock/out of stock) ->
   Frontend displays products with real-time stock information

2. **Payment Processing Flow:**  
   POST /api/payment -> User selects products -> System calculates total amount ->
   System validates inventory -> System processes payment -> System updates inventory ->
   System returns success with transaction details
   
   Key Operations: Inventory check -> Payment validation -> Stock update -> Transaction logging

3. **Payment Cancel/Failure Flow:**  
   POST /api/payment/cancel OR Payment fails -> System rolls back inventory changes ->
   System returns inserted money -> System logs failure reason -> System resets transaction state ->
   System returns to IDLE state
   
   Error Scenarios: Insufficient funds, out of stock, user cancellation, system failure

4. **Admin Stocking Flow:**  
   POST /api/admin/stock (Admin only) -> System validates admin credentials ->
   System updates product quantities -> System resets out-of-stock alerts ->
   System logs restocking activity -> System returns updated inventory status
   
   Admin Operations: Add products, update quantities, view sales reports, collect cash

5. **Power Failure Recovery Flow:**  
   System startup -> RecoveryController.checkAndRecover() ->
   RecoveryService.performRecovery() -> System checks pending recoveries ->
   System executes recovery based on interrupted state -> System returns to IDLE state
   
   Recovery Scenarios: PROCESSING_PAYMENT = refund money, DISPENSING = complete dispensing + change

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **VendingMachineController** (Product & Inventory Management)
   - List\<Product\> getAvailableProducts(int machineId)
   - Product getProductDetails(int machineId, int productId)
   - InventoryStatus getInventoryStatus(int machineId)

2. **PaymentController** (Payment Processing)
   - Transaction processPayment(int machineId, PaymentRequest request)
   - void cancelPayment(int machineId, int transactionId)
   - PaymentStatus getPaymentStatus(int machineId, int transactionId)
   - List\<Transaction\> getTransactionHistory(int machineId)

3. **RecoveryController** (Power Failure Recovery)
   - void checkAndRecover(int machineId)
   - RecoveryStatus getRecoveryStatus(int machineId)
   - void markRecoveryComplete(int machineId, int recoveryId)

4. **AdminController** (Admin Operations)
   - void restockProduct(int machineId, RestockRequest request)
   - void collectCash(int machineId)
   - SalesReport getSalesReport(int machineId, DateRange dateRange)

### SERVICES:

1. **VendingMachineService** (Product & Inventory Management)
   - List\<Product\> getAvailableProducts(int machineId)
   - Product getProductDetails(int machineId, int productId)
   - InventoryStatus getInventoryStatus(int machineId)
   - void updateInventory(int machineId, int productId, int quantity)

2. **PaymentService** (Payment Processing)
   - Transaction processPayment(int machineId, PaymentRequest request)
   - void cancelPayment(int machineId, int transactionId)
   - PaymentStatus getPaymentStatus(int machineId, int transactionId)
   - void updateCashBox(int machineId, Map\<Denomination, Integer\> denominations)

3. **RecoveryService** (Power Failure Recovery)
   - void performRecovery(int machineId)
   - RecoveryStatus getRecoveryStatus(int machineId)
   - void createRecoveryEntry(int machineId, int transactionId, VendingMachineState state)
   - void markRecoveryComplete(int machineId, int recoveryId)

4. **AdminService** (Admin Operations)
   - void restockProduct(int machineId, RestockRequest request)
   - void collectCash(int machineId)
   - SalesReport getSalesReport(int machineId, DateRange dateRange)

### REPOSITORIES:

1. **VendingMachineRepository**
   - VendingMachine findById(int machineId)
   - void updateInventory(int machineId, int productId, int quantity)

2. **PaymentRepository**
   - void saveTransaction(Transaction transaction)
   - List\<Transaction\> findByMachine(int machineId)
   - Transaction findById(int transactionId)
   - void updateCashBox(int machineId, Map\<Denomination, Integer\> denominations)

3. **ProductRepository**
   - List\<Product\> findByMachine(int machineId)
   - Product findById(int productId)

4. **RecoveryRepository**
   - void saveRecovery(Recovery recovery)
   - List\<Recovery\> findPendingRecoveries(int machineId)
   - Recovery findById(int recoveryId)
   - void markComplete(int recoveryId)

### STATE PATTERN IMPLEMENTATION:

1. **VendingMachineState** (Interface)
   - void processPayment(VendingMachine machine, PaymentRequest request)
   - void cancelPayment(VendingMachine machine, int transactionId)
   - String getStateName()

2. **IdleState** (Concrete State)
   - void processPayment(VendingMachine machine, PaymentRequest request) // Valid transition
   - void cancelPayment(VendingMachine machine, int transactionId) // No change
   - String getStateName() // Returns "IDLE"

3. **ProcessingPaymentState** (Concrete State)
   - void processPayment(VendingMachine machine, PaymentRequest request) // Invalid
   - void cancelPayment(VendingMachine machine, int transactionId) // Valid - return to IDLE
   - String getStateName() // Returns "PROCESSING_PAYMENT"

4. **DispensingState** (Concrete State)
   - void processPayment(VendingMachine machine, PaymentRequest request) // Invalid
   - void cancelPayment(VendingMachine machine, int transactionId) // Invalid
   - String getStateName() // Returns "DISPENSING"

5. **VendingMachine** (Context - Uses State Pattern)
   - id: int
   - location: String
   - currentState: VendingMachineState
   - currentTransaction: Transaction
   - inventory: Map\<Product, Integer\>
   - cashBox: CashBox
   - isOperational: boolean
   - void setState(VendingMachineState newState)
   - Transaction processPayment(PaymentRequest request)
   - void cancelPayment(int transactionId)
   - String getCurrentStateName()

---

## STEP-5: CORE USE CASES AND METHODS

1. **Product Listing Use Case:**  
   GET /api/products -> VendingMachineController.getAvailableProducts() ->
   VendingMachineService.getAvailableProducts() -> Check inventory levels -> Return product list with availability

2. **Payment Processing Use Case:**  
   POST /api/payment -> PaymentController.processPayment() ->
   PaymentService.processPayment() -> Validate inventory -> Check state (must be IdleState) ->
   Change state to ProcessingPaymentState -> Process payment -> Update inventory ->
   Change state to DispensingState -> Dispense product -> Change state back to IdleState -> Return transaction

3. **Payment Cancel/Failure Use Case:**  
   POST /api/payment/cancel -> PaymentController.cancelPayment() ->
   PaymentService.cancelPayment() -> Rollback inventory -> Refund money -> Log failure ->
   Change state back to IdleState -> Reset state

4. **Admin Stocking Use Case:**  
   POST /api/admin/stock -> AdminController.restockProduct() ->
   AdminService.restockProduct() -> Validate admin -> Check current state ->
   Update inventory -> Reset alerts -> Log activity -> Ensure state is IdleState

5. **Power Failure Recovery Use Case:**  
   System startup -> RecoveryController.checkAndRecover() ->
   RecoveryService.performRecovery() -> Check pending recoveries -> Check last known state ->
   Execute recovery based on interrupted state -> Reset to safe state (IdleState) -> Mark complete

---

## STEP-6: OOPS PRINCIPLES AND DESIGN PATTERNS USED

### DESIGN PATTERNS USED:

1. **Repository Pattern** - for data access abstraction
2. **Service Layer Pattern** - for business logic separation
3. **State Pattern** - for vending machine state management
4. **RESTful API Design** - for clean HTTP endpoints
5. **Controller Separation Pattern** - for better separation of concerns

### OOP PRINCIPLES APPLIED:

1. **Single Responsibility** - each controller has one clear purpose
2. **Open/Closed** - easy to extend with new product types
3. **Encapsulation** - domain objects encapsulate their data and behavior
4. **Dependency Inversion** - services depend on repositories, not concrete implementations

---

## STEP-7: HANDLE EDGE CASES

1. **Concurrent Access** - State pattern ensures thread safety and proper transaction queuing
2. **Change Calculation** - Greedy algorithm to calculate optimal change combination
3. **Inventory Management** - Real-time inventory updates with threshold-based restocking alerts
4. **Transaction Recovery** - Transaction logging for recovery after system failures
5. **Error Handling** - Comprehensive error handling with user-friendly messages

---

## STEP-8: CLASS DIAGRAM AND RELATIONSHIPS

1. **Association** - I work with you
2. **Aggregation** - I have you, but you are not mine
3. **Composition** - You are mine and only mine

---

## STEP-9: FUTURE ENHANCEMENTS

1. **Multiple Vending Machines**
   - Centralized inventory management
   - Remote monitoring and control
   - Sales analytics across machines

2. **Advanced Payment Methods**
   - Credit/debit card support
   - Mobile payment integration
   - Digital wallet support

3. **Enhanced Inventory Management**
   - Expiry date tracking
   - Temperature monitoring for perishables
   - Automatic reordering

4. **Advanced Analytics**
   - Sales trend analysis
   - Popular product identification
   - Revenue optimization

# ATM MACHINE LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. Each user has one bank account at the bank
2. User should be able to insert card and perform transactions
3. ATM should authenticate user on the basis of the PIN entered
4. Once authenticated user should be able to perform transactions:
   - view balance
   - deposit cash
   - withdraw cash
5. User can perform only one transaction at any given time (per session)
6. At the end of a transaction, appropriate success/failure messages should be displayed
7. Card should be returned when user exits
8. Admin should be able to refill cash, audit inventory, take ATM in/out of service, and view logs

### EDGE CASES:

1. Wrong PIN attempts
2. Any withdrawl limit?
3. How make sure the ATM maintains consistency of balance? Lock or last moment check?
4. What happens in power failure?
5. Notes mismatch while depositing?
6. Handle floating point errors?

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **ATM**
   - id: String [PK]
   - location: String
   - isOnline: boolean
   - currentState: ATMState
   - cashDrawer: CashDrawer
   - currentSession: Session

2. **Card**
   - id: String [PK]
   - accountId: String [FK]
   - expiry: String (MM/YY)
   - isBlocked: boolean
   - pinRetriesLeft: int

3. **Account**
   - id: String [PK]
   - holderName: String
   - balanceMinorUnits: long
   - dailyWithdrawalLimitMinor: long
   - dailyWithdrawalUsedMinor: long
   - isActive: boolean

4. **Transaction**
   - id: String [PK]
   - atmId: String [FK]
   - sessionId: String [FK]
   - accountId: String [FK]
   - type: TransactionType (WITHDRAW, DEPOSIT, BALANCE)
   - amountMinorUnits: long
   - status: TransactionStatus (PENDING, SUCCESS, FAILED)
   - dispensedNotes: Map\<Denomination, Integer\>
   - depositedNotes: Map\<Denomination, Integer\>
   - createdAt: long
   - timeoutAt: long

5. **CashDrawer**
   - atmId: String [FK]
   - notesByDenomination: Map\<Denomination, Integer\>

6. **Denomination (Enum)**
   - 500, 200, 100

7. **TransactionType (Enum)**
   - WITHDRAW, DEPOSIT, BALANCE

8. **TransactionStatus (Enum)**
   - PENDING, SUCCESS, FAILED

9. **AdminUser**
   - id: String [PK]
   - name: String
   - pinHash: String
   - isActive: boolean

10. **Session**
    - id: String [PK]
    - atmId: String [FK]
    - cardId: String [FK]
    - accountId: String [FK]
    - startTime: long
    - endTime: long
    - isActive: boolean
    - currentTransactionId: String [FK] (nullable)
    - transactionType: String
    - amount: long

---

## STEP-3: VISUALIZE INTERACTION FLOWS

1. **Card Operations:**
   - Insert card
   - Eject card
   - Authenticate card

2. **Session Operations:**
   - Start session
   - End session

3. **Transaction Operations:**
   - Show balance
   - Withdraw cash
   - Deposit cash
   - Acknowledge transaction

4. **ATM Operations:**
   - Take offline
   - Bring online
   - Audit cash

5. **Admin Operations:**
   - Login admin
   - Refill cash
   - Audit cash

6. **State-Driven Flow:**
   - States orchestrate services and auto-transition
   - Invalid operations throw exceptions
   - Controllers handle exceptions and return responses

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **ATMController** (ATM Management)
   - void takeOffline(String atmId)
   - void bringOnline(String atmId)
   - CashDrawer auditCash(String atmId)

2. **CardController** (Card Operations)
   - boolean insertCard(String cardId)
   - void ejectCard(String atmId)
   - boolean authenticateCard(String cardId, String pin)

3. **SessionController** (Session Management)
   - Session startSession(String atmId, String cardId)
   - void endSession(String sessionId)

4. **TransactionController** (Transaction Operations)
   - Transaction showBalance(String sessionId)
   - Transaction withdrawCash(String sessionId, long amountMinorUnits)
   - Transaction depositCash(String sessionId, Map\<Denomination, Integer\> notes)
   - void acknowledgeTransaction(String transactionId)

5. **AdminController** (Admin Operations)
   - boolean loginAdmin(String adminId, String pin)
   - void refillCash(String atmId, Map\<Denomination, Integer\> notes)
   - CashDrawer auditCash(String atmId)

### SERVICES:

1. **ATMService** (ATM Management)
   - void takeOffline(String atmId)
   - void bringOnline(String atmId)
   - CashDrawer auditCash(String atmId)

2. **CardService** (Card Operations)
   - boolean insertCard(String atmId, String cardId)
   - void ejectCard(String atmId)
   - boolean validateCard(String cardId)
   - boolean authenticateCard(String cardId, String pin)

3. **SessionService** (Session Management)
   - Session startSession(String atmId, String cardId)
   - void endSession(String sessionId)
   - Session getCurrentSession(String atmId)
   - void handleSessionTimeout(String sessionId)

4. **TransactionService** (Transaction Operations)
   - Transaction showBalance(String sessionId)
   - Transaction withdrawCash(String sessionId, long amountMinorUnits)
   - Transaction depositCash(String sessionId, Map\<Denomination, Integer\> notes)
   - void acknowledgeTransaction(String transactionId)
   - boolean validateTransaction(Transaction transaction)

5. **AdminService** (Admin Operations)
   - boolean loginAdmin(String adminId, String pin)
   - void refillCash(String atmId, Map\<Denomination, Integer\> notes)
   - CashDrawer auditCash(String atmId)

### REPOSITORIES:

1. **ATMRepository**
   - ATM save(ATM atm)
   - Optional\<ATM\> findById(String atmId)
   - List\<ATM\> findAll()
   - void updateATMState(String atmId, ATMState state)

2. **CardRepository**
   - Card save(Card card)
   - Optional\<Card\> findById(String cardId)
   - void updatePinRetries(String cardId, int retriesLeft)
   - void blockCard(String cardId)

3. **AccountRepository**
   - Account save(Account account)
   - Optional\<Account\> findById(String accountId)
   - void updateBalance(String accountId, long newBalance)
   - void updateDailyWithdrawalUsed(String accountId, long amountUsed)

4. **TransactionRepository**
   - Transaction save(Transaction transaction)
   - Optional\<Transaction\> findById(String transactionId)
   - List\<Transaction\> findBySession(String sessionId)
   - List\<Transaction\> findByATMAndTimeRange(String atmId, long startTime, long endTime)
   - void updateTransactionStatus(String transactionId, TransactionStatus status)

5. **CashDrawerRepository**
   - CashDrawer save(CashDrawer cashDrawer)
   - Optional\<CashDrawer\> findByATMId(String atmId)
   - void updateCashInventory(String atmId, Map\<Denomination, Integer\> notes)

6. **SessionRepository**
   - Session save(Session session)
   - Optional\<Session\> findById(String sessionId)
   - Optional\<Session\> findActiveByATM(String atmId)
   - void endSession(String sessionId)

7. **AdminUserRepository**
   - AdminUser save(AdminUser adminUser)
   - Optional\<AdminUser\> findById(String adminId)
   - boolean validateAdminCredentials(String adminId, String pinHash)

### STATE PATTERN IMPLEMENTATION:

1. **ATMState** (State Interface)
   - void insertCard(ATM atm, String cardId) throws InvalidATMOperationException
   - void ejectCard(ATM atm) throws InvalidATMOperationException
   - void enterPin(ATM atm, String pin) throws InvalidATMOperationException
   - void selectTransaction(ATM atm, TransactionType type) throws InvalidATMOperationException
   - void processTransaction(ATM atm, long amount) throws InvalidATMOperationException
   - void endSession(ATM atm) throws InvalidATMOperationException
   - ATMState next(ATM atm) // compute next state after a successful operation

### STRATEGY PATTERN IMPLEMENTATION:

1. **TransactionStrategy** (Strategy Interface)
   - Transaction processTransaction(String sessionId, long amount, Map\<Denomination, Integer\> notes)

2. **WithdrawalStrategy** (Concrete Strategy)
   - Transaction processTransaction(String sessionId, long amount, Map\<Denomination, Integer\> notes)

3. **DepositStrategy** (Concrete Strategy)
   - Transaction processTransaction(String sessionId, long amount, Map\<Denomination, Integer\> notes)

4. **BalanceInquiryStrategy** (Concrete Strategy)
   - Transaction processTransaction(String sessionId, long amount, Map\<Denomination, Integer\> notes)

---

## STEP-5: CORE USE CASES AND METHODS

---

## STEP-6: OOPS PRINCIPLES AND DESIGN PATTERNS USED

### DESIGN PATTERNS USED:

1. **State Pattern** - for managing ATM operational states
2. **Repository Pattern** - for data access abstraction
3. **Service Layer Pattern** - for business logic separation
4. **Strategy Pattern** - for different transaction processing strategies
5. **Factory Pattern** - for creating different transaction types
6. **Command Pattern** - for encapsulating transaction operations

### OOP PRINCIPLES APPLIED:

1. **Single Responsibility** - each class has one clear purpose
2. **Open/Closed** - easy to extend with new transaction types or states
3. **Liskov Substitution** - all state implementations are interchangeable
4. **Interface Segregation** - focused interfaces for different concerns
5. **Dependency Inversion** - high-level modules depend on abstractions
6. **Encapsulation** - domain objects hide internal implementation details
7. **Polymorphism** - different transaction types handled uniformly

---

## STEP-7: EDGE CASE SOLUTIONS

1. **Wrong PIN attempts** – Track retries in Card entity; block after 3 failed attempts; clear count on success
2. **Withdrawal limits** – Validate daily limit, ATM cash, transaction max, and denominations; use greedy algorithm
3. **ATM balance consistency** – Use database transactions, optimistic locking, rollback on failure, maintain audit trail
4. **Power failure handling** – Graceful shutdown, eject card, persist state, recover on restoration, complete pending transactions
5. **Notes mismatch during deposit** – Validate denominations, count notes, reject if mismatch, log discrepancies
6. **Floating point errors** – Use integer minor units, round display, BigDecimal for critical calculations
7. **Invalid operation for current state** – Throw InvalidATMOperationException; map to error response; state remains unchanged

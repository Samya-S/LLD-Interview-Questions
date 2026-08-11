# DIGITAL WALLET LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. The system should allow users to create wallets with unique account numbers.
2. The system should enable transfer of funds between wallets using TUF currency.
3. The system should allow users to add money to their wallets through payment gateway integration.
4. The system should enforce minimum balance constraint - account balance cannot drop below TUF 0.00.
5. The system should enforce minimum transfer amount constraint - smallest transferable amount is 0.01 TUF.
6. Users should be able to view account statements showing all transactions for their wallet.
7. The system should maintain a complete audit trail of all transactions.
8. The system should handle concurrent transactions and ensure data consistency.
9. Send notifications to email.
10. Admin can suspend, reopen and close wallets.

### EDGE CASES:

1. How to handle concurrent transactions on the same wallet?
2. Which locking mechanism to use?
3. OTP confirmation on transactions?
4. Creating accounts has any authentication?
5. Maintain transaction limits?
6. Withdraw options to external source?

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **Wallet** (Core Entity)
   - id: int [PK]
   - accountNumber: String [UNIQUE]
   - balance: long (Stored as integer: actual amount * 100, e.g., 100.50 TUF = 10050)
   - userId: int [FK to User, UNIQUE] (One-to-One: One user has one wallet)
   - status: WalletStatus (Enum: ACTIVE, SUSPENDED, CLOSED)
   - createdAt: LocalDateTime
   - updatedAt: LocalDateTime

2. **User**
   - id: int [PK]
   - username: String [UNIQUE]
   - email: String [UNIQUE]
   - name: String
   - (One-to-One relationship: One user has one wallet, accessed via Wallet.userId)

3. **Transaction** (Core Entity)
   - id: int [PK]
   - transactionId: String [UNIQUE]
   - fromWalletId: int [FK to Wallet, NULLABLE] (NULL for DEPOSIT)
   - toWalletId: int [FK to Wallet, NULLABLE] (NULL for WITHDRAWAL)
   - amount: long (Stored as integer: actual amount * 100, e.g., 100.50 TUF = 10050)
   - transactionType: TransactionType (Enum: TRANSFER, DEPOSIT, WITHDRAWAL)
   - status: TransactionStatus (Enum: PENDING, COMPLETED, FAILED, CANCELLED)
   - paymentGatewayId: String [NULLABLE] (Payment gateway transaction ID for DEPOSIT)
   - paymentMethod: String [NULLABLE] (Credit Card, Debit Card, UPI, etc.)
   - description: String
   - timestamp: LocalDateTime

4. **TransactionStatus** (Enum)
   - PENDING, COMPLETED, FAILED, CANCELLED

5. **TransactionType** (Enum)
   - TRANSFER, DEPOSIT, WITHDRAWAL

6. **WalletStatus** (Enum)
   - ACTIVE, SUSPENDED, CLOSED

### RESPONSE DTOs (Not Stored Entities):

7. **AccountStatement** (Response DTO - Built from queries)
   - walletId: int
   - walletAccountNumber: String
   - transactions: List\<Transaction\> (Queried from TransactionRepository)
   - startDate: LocalDateTime (Optional filter)
   - endDate: LocalDateTime (Optional filter)
   - currentBalance: long (From Wallet entity)
   - Note: This is a response object built on-the-fly, not a stored entity

### REQUEST DTOs:

8. **TransactionRequest**
   - fromAccountNumber: String
   - toAccountNumber: String
   - amount: long (Stored as integer: actual amount * 100)
   - description: String

9. **AddMoneyRequest**
   - accountNumber: String
   - amount: long (Stored as integer: actual amount * 100)
   - paymentMethod: String (Credit Card, Debit Card, UPI)
   - paymentGateway: String (Stripe, Razorpay, PayPal)
   - paymentDetails: Map\<String, String\> (Card details, etc.)

---

## STEP-3: VISUALIZE INTERACTION FLOWS

1. **Wallet Creation:**
   - POST /api/wallets -\> WalletController.createWallet(userId) -\>
   - WalletService.createWallet(userId) -\> Validate one-wallet-per-user -\>
   - Create Wallet (status=ACTIVE, balance=0) -\> Return Wallet

2. **Add Money (Deposit) - Two Phase:**
   - **Phase 1 - Initiate Deposit:**
     - POST /api/wallets/{accountNumber}/deposit -\> TransactionController.initiateDeposit(request) -\>
     - TransactionService.initiateDeposit(request) -\> Create Transaction(PENDING, type=DEPOSIT) -\>
     - PaymentGatewayRouter.selectProvider(request.paymentGateway, amount, "TUF")
     - -\> provider.initiatePayment(...) -\> Return providerRef to client
   - **Phase 2 - Payment Callback:**
     - POST /api/payments/callback -\> TransactionController.handlePaymentCallback(providerRef, status) -\>
     - TransactionService.handleDepositCallback(providerRef, status) -\> Verify signature -\>
     - On SUCCESS: credit wallet, mark transaction COMPLETED -\> Notify user
     - On FAILURE/TIMEOUT: mark transaction FAILED -\> No balance change

3. **Transfer Funds:**
   - POST /api/transactions/transfer -\> TransactionController.transfer(request) -\>
   - TransactionService.transfer(request) -\> Acquire locks on both wallets (see STEP-7.5) -\>
   - Validate wallets active + amount \>= min + sufficient balance -\>
   - Debit source -\> Credit destination -\> Create Transaction(COMPLETED, type=TRANSFER) -\> Notify user

4. **Withdraw:**
   - POST /api/wallets/{accountNumber}/withdraw -\> TransactionController.withdraw(accountNumber, amount) -\>
   - TransactionService.withdraw(...) -\> Validate limits -\> Create Transaction(PENDING, type=WITHDRAWAL) -\>
   - External payout integration (out of scope) -\> On success: debit wallet and mark COMPLETED; else FAILED

5. **Account Statement:**
   - GET /api/wallets/{accountNumber}/statement?start&end -\> WalletController.getStatement(...) -\>
   - TransactionService.getStatement(accountNumber, start, end) -\> Build AccountStatement -\> Return

6. **Notifications:**
   - On transaction completion -\> NotificationRouter.send("email", NotificationMessage{to, subject, body})

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **WalletController**
   - Wallet createWallet(int userId)
   - long getBalance(String accountNumber)
   - AccountStatement getStatement(String accountNumber, LocalDateTime start, LocalDateTime end)

2. **TransactionController**
   - Transaction transfer(TransactionRequest request)
   - Transaction initiateDeposit(AddMoneyRequest request)
   - void handlePaymentCallback(String providerRef, TransactionStatus status)
   - Transaction withdraw(String accountNumber, long amount, String description)

3. **AdminController**
   - void suspendWallet(String accountNumber)
   - void closeWallet(String accountNumber)
   - void reopenWallet(String accountNumber)

### SERVICES:

1. **WalletService**
   - Wallet createWallet(int userId)
   - Wallet getByAccountNumber(String accountNumber)
   - boolean isActive(String accountNumber)

2. **TransactionService**
   - Transaction transfer(TransactionRequest request)
   - Transaction initiateDeposit(AddMoneyRequest request)
   - void handleDepositCallback(String providerRef, TransactionStatus status)
   - Transaction withdraw(String accountNumber, long amount, String description)
   - AccountStatement getStatement(String accountNumber, LocalDateTime start, LocalDateTime end)

3. **PaymentGatewayService** (Strategy)
   - String initiatePayment(String accountNumber, long amount, String paymentMethod, String paymentGateway, Map\<String, String\> paymentDetails)
   - boolean verifyCallback(String providerRef, TransactionStatus status)

4. **LockService** (Distributed)
   - boolean acquire(String key, long timeoutMs)
   - void release(String key)

5. **NotificationRouter** (Simple registry + dispatch)
   - void register(String channelName, NotificationChannel channel)
   - void send(String channelName, NotificationMessage message)

### REPOSITORIES:

1. **WalletRepository**
   - Optional\<Wallet\> findByAccountNumber(String accountNumber)
   - Wallet save(Wallet wallet)
   - void updateBalance(int walletId, long deltaMinor) // used within DB transactions

2. **UserRepository**
   - Optional\<User\> findById(int userId)

3. **TransactionRepository**
   - Transaction save(Transaction transaction)
   - List\<Transaction\> findByWalletAndRange(int walletId, LocalDateTime start, LocalDateTime end)
   - Optional\<Transaction\> findByTransactionId(String transactionId)

### STRATEGY PATTERN IMPLEMENTATIONS:

**PAYMENT GATEWAY STRATEGY** (Scalable Providers):
- **PaymentGatewayProvider** (Strategy Interface)
  - String getName()
  - String initiatePayment(String accountNumber, long amount, String paymentMethod, Map\<String, String\> paymentDetails)
  - boolean verifyCallback(String providerRef, TransactionStatus status)
- **StripePaymentGatewayProvider** (Concrete)
- **RazorpayPaymentGatewayProvider** (Concrete)
- **PayPalPaymentGatewayProvider** (Concrete)
- **MockPaymentGatewayProvider** (Concrete - simulation)
- **PaymentGatewayRouter** (Context/Router)
  - Map\<String, PaymentGatewayProvider\> providers
  - String selectProvider(String preferredGateway, long amount, String currency)
  - PaymentGatewayProvider resolve(String gatewayName)

**NOTIFICATION STRATEGY** (Keep It Simple):
- **NotificationChannel** (Interface)
  - void send(NotificationMessage message)
- **NotificationRouter** (Registry + Single-dispatch)
  - Map\<String, NotificationChannel\> channels
  - void register(String channelName, NotificationChannel channel)
  - void send(String channelName, NotificationMessage message)
- **EmailNotificationChannel** (SMTP/API)
- **SmsNotificationChannel** (e.g., Twilio)
- **NotificationMessage** (to, subject, body)

---

## DIFFERENT TYPES OF LOCKING

1. **Database transaction with row level locking** (Pessimistic Locking)
2. **Optimistic Locking** with version field
   - *Note:* assumes conflicts are rare and checks for conflicts at commit time.
3. **Distributed Locking** (Pessimistic in nature)
   - *Note:* assumes conflicts are likely and locks resources before modifying them.

### 1. Database transaction with row level locking

**Description:**
- Use database transaction with high isolation level (SERIALIZABLE or REPEATABLE_READ)
- Acquire exclusive locks on wallet rows using SELECT FOR UPDATE
- Locks are held until transaction commits/rolls back

**Example Flow:**
1. BEGIN TRANSACTION
2. SELECT balance FROM wallet WHERE id = 1 FOR UPDATE (locks row)
3. SELECT balance FROM wallet WHERE id = 2 FOR UPDATE (locks row)
4. Validate: wallet1.balance \>= amount
5. UPDATE wallet SET balance = balance - amount WHERE id = 1
6. UPDATE wallet SET balance = balance + amount WHERE id = 2
7. INSERT INTO transaction (...) VALUES (...)
8. COMMIT TRANSACTION

**Benefits:**
- Database ensures atomicity (all or nothing)
- Prevents concurrent modifications
- Works with any SQL database (PostgreSQL, MySQL, Oracle, etc.)
- Language-independent

**Limitations:**
- Locks held for entire transaction duration
- Can cause deadlocks if locks are acquired in different order (e.g. T1, T2)
- May reduce throughput under high concurrency

### 2. Optimistic locking with version field

**Description:**
- Add version/timestamp field to Wallet entity
- Read wallet with current version
- Before update, verify version hasn't changed
- If version changed, transaction fails (retry or reject)

**Example Flow:**
1. READ wallet (id=1, balance=100, version=5)
2. Validate: balance \>= amount
3. UPDATE wallet SET balance = balance - amount, version = version + 1 WHERE id = 1 AND version = 5
4. If update affects 0 rows -\> Version changed -\> Retry or fail
5. If update affects 1 row -\> Success -\> Continue with toWallet update

**Benefits:**
- No long-held locks
- Better for high-read scenarios
- Works with any database and language
- Retry mechanism handles conflicts

**Limitations:**
- Requires retry logic
- Can fail multiple times before success
- More complex error handling

### 3. Distributed Locking

**Description:**
- Use distributed lock manager (Redis, Memcached, etc.)
- Acquire lock on wallet ID before transaction
- Release lock after transaction completes
- Set lock expiration to prevent deadlocks

**Example Flow:**
1. ACQUIRE LOCK("wallet_1", timeout=5 seconds)
   - If lock acquired -\> Proceed
   - If lock exists -\> Wait (block) until lock is released or timeout expires
   - If timeout expires -\> Return error
2. READ wallet balance
3. Validate: balance \>= amount
4. UPDATE wallet balances
5. CREATE transaction record
6. RELEASE LOCK("wallet_1")

**Benefits:**
- Works across multiple application servers (distributed systems)
- Language and database agnostic
- Prevents concurrent access at application level
- Lock expiration prevents deadlocks
- Can handle high concurrency with proper lock management

**Limitations:**
- Requires external lock service (Redis, etc.)
- Network latency for lock operations
- Need to handle lock expiration and renewal

---

## STEP-5: CORE USE CASES AND METHODS

1. **Transfer Use Case:**
   - transfer(request) -\>
   - Validate: non-negative amount, amount \>= 1 (minor unit), not self-transfer
   - Fetch wallets by account numbers; ensure both ACTIVE and not SUSPENDED/CLOSED
   - Acquire distributed locks on both wallet IDs in sorted order
   - Within DB transaction:
     - Ensure fromWallet.balance \>= amount
     - Update fromWallet.balance -= amount
     - Update toWallet.balance += amount
     - Create Transaction(COMPLETED, type=TRANSFER)
   - Release locks; NotificationRouter.send("email", NotificationMessage{to, subject, body})

2. **Deposit Use Case:**
   - initiateDeposit(request) -\>
   - Validate: amount \>= minimum, wallet ACTIVE
   - Create Transaction(PENDING, type=DEPOSIT, paymentGateway, paymentMethod)
   - PaymentGatewayRouter.selectProvider(request.paymentGateway, amount, "TUF") -\> resolve provider
   - Call provider.initiatePayment(...) -\> return providerRef to client
   
   - handleDepositCallback(providerRef, status) -\>
   - Verify callback authenticity (signature/secret)
   - If SUCCESS and not already processed:
     - Credit wallet within DB transaction
     - Mark Transaction COMPLETED, set providerRef
     - NotificationRouter.send("email", NotificationMessage{to, subject, body})
   - If FAILURE/TIMEOUT: mark Transaction FAILED (idempotent)

3. **Withdraw Use Case (Optional Stub):**
   - withdraw(accountNumber, amount) -\>
   - Validate KYC/limits and wallet ACTIVE
   - Create Transaction(PENDING, type=WITHDRAWAL)
   - External payout provider integration (out of scope)
   - On success: debit wallet and mark COMPLETED; else FAILED

4. **Account Statement Use Case:**
   - getStatement(accountNumber, start, end) -\>
   - Lookup wallet -\> Query TransactionRepository.findByWalletAndRange(...)
   - Build AccountStatement with current balance + transactions
   - Return DTO (no persistence)

---

## STEP-6: OOP PRINCIPLES AND DESIGN PATTERNS

### DESIGN PATTERNS USED:

1. **Repository Pattern** - data access abstraction for Wallet/User/Transaction
2. **Service Layer** - business logic separation (WalletService/TransactionService)
3. **Strategy Pattern** - PaymentGatewayProvider + Router; NotificationChannel + Router
4. **Domain Events (lightweight) or direct router call** - transaction completion triggers notifications
5. **RESTful API Design** - clean resource-oriented endpoints

### OOP PRINCIPLES APPLIED:

1. **Single Responsibility** - each service focuses on one concern
2. **Open/Closed** - add new payment gateways or notification channels without modifying core logic
3. **Encapsulation** - wallet balance updates only through TransactionService
4. **Dependency Inversion** - services depend on interfaces (repositories, gateway, notification, lock)
5. **Idempotency** - callbacks/requests can be safely retried without double credit/debit

package main;

import controller.*;
import domain.*;
import repository.impl.*;
import service.*;
import java.util.HashMap;
import java.util.Map;

public class ATMSimulation {
    public static void main(String[] args) {
        // Initialize repositories
        ATMRepositoryImpl atmRepository = new ATMRepositoryImpl();
        CardRepositoryImpl cardRepository = new CardRepositoryImpl();
        AccountRepositoryImpl accountRepository = new AccountRepositoryImpl();
        TransactionRepositoryImpl transactionRepository = new TransactionRepositoryImpl();
        SessionRepositoryImpl sessionRepository = new SessionRepositoryImpl();
        CashDrawerRepositoryImpl cashDrawerRepository = new CashDrawerRepositoryImpl();
        AdminUserRepositoryImpl adminUserRepository = new AdminUserRepositoryImpl();

        // Initialize services
        ATMService atmService = new ATMService(atmRepository, cashDrawerRepository);
        CardService cardService = new CardService(cardRepository);
        SessionService sessionService = new SessionService(sessionRepository);
        TransactionService transactionService = new TransactionService(transactionRepository);
        AdminService adminService = new AdminService(adminUserRepository, cashDrawerRepository, transactionRepository);

        // Initialize controllers
        CardController cardController = new CardController(atmService);
        SessionController sessionController = new SessionController(sessionService, atmService);
        TransactionController transactionController = new TransactionController(transactionService, sessionService,
                atmService);
        AdminController adminController = new AdminController(adminService);

        // Setup test data
        setupTestData(atmRepository, cardRepository, accountRepository, adminUserRepository, cashDrawerRepository);

        // Wire state orchestration services to ATM demo instance
        ATM demoAtm = atmService.getATM("ATM_001");
        if (demoAtm != null) {
            demoAtm.attachServices(cardService, sessionService, transactionService);
        }

        // Simulate ATM operations
        System.out.println("=== ATM Machine Simulation ===");

        String atmId = "ATM_001";
        String cardId = "CARD_001";

        // 1. Card Operations
        System.out.println("\n1. Card Operations:");
        boolean cardInserted = cardController.insertCard(atmId, cardId);
        System.out.println("Card inserted: " + cardInserted);

        // 2. Session Management
        System.out.println("\n2. Session Management:");
        Session session = sessionController.startSession(atmId, cardId);
        System.out.println("Session started: " + (session != null ? session.getId() : "N/A"));

        // 3. Authentication
        System.out.println("\n3. Authentication:");
        boolean authenticated = cardController.authenticateCard(atmId, cardId, "1234");
        System.out.println("Authentication: " + authenticated);

        // 4. Transaction Operations
        System.out.println("\n4. Transaction Operations:");

        // Show balance
        Transaction balanceTxn = transactionController.showBalance(session.getId());
        System.out.println("Balance inquiry: " + (balanceTxn != null ? balanceTxn.getStatus() : "Failed"));

        // Withdraw cash
        long withdrawAmount = 10000; // $100
        Transaction withdrawTxn = transactionController.withdrawCash(session.getId(), withdrawAmount);
        System.out.println("Withdrawal: " + (withdrawTxn != null ? withdrawTxn.getStatus() : "Failed"));

        // Deposit cash
        Map<Denomination, Integer> depositNotes = new HashMap<>();
        depositNotes.put(Denomination.ONE_HUNDRED, 2); // $200
        Transaction depositTxn = transactionController.depositCash(session.getId(), depositNotes);
        System.out.println("Deposit: " + (depositTxn != null ? depositTxn.getStatus() : "Failed"));

        // 5. Admin Operations
        System.out.println("\n5. Admin Operations:");
        boolean adminLoggedIn = adminController.loginAdmin("ADMIN_001", "1234");
        System.out.println("Admin login: " + adminLoggedIn);

        if (adminLoggedIn) {
            CashDrawer cashDrawer = adminController.auditCash(atmId);
            if (cashDrawer != null) {
                System.out.println("Cash audit - Total: $" + (cashDrawer.getTotalCash() / 100));
            } else {
                System.out.println("Cash audit - No cash drawer found for ATM: " + atmId);
            }
        }

        // End session
        sessionController.endSession(session.getId());
        cardController.ejectCard(atmId);
        System.out.println("Session ended and card ejected");
    }

    private static void setupTestData(ATMRepositoryImpl atmRepository,
            CardRepositoryImpl cardRepository,
            AccountRepositoryImpl accountRepository,
            AdminUserRepositoryImpl adminUserRepository,
            CashDrawerRepositoryImpl cashDrawerRepository) {
        // Create ATM
        ATM atm = new ATM("ATM_001", "Main Street Branch");
        atmRepository.save(atm);

        // Create CashDrawer for ATM
        CashDrawer cashDrawer = new CashDrawer("ATM_001");
        cashDrawer.addNotes(Denomination.ONE_HUNDRED, 50); // $5000
        cashDrawer.addNotes(Denomination.TWO_HUNDRED, 25); // $5000
        cashDrawer.addNotes(Denomination.FIVE_HUNDRED, 10); // $5000
        cashDrawerRepository.save(cashDrawer);

        // Create Card
        Card card = new Card("CARD_001", "ACC_001", "12/25");
        cardRepository.save(card);

        // Create Account
        Account account = new Account("ACC_001", "John Doe", 100000); // $1000
        accountRepository.save(account);

        // Create Admin User
        AdminUser admin = new AdminUser("ADMIN_001", "Admin User", "1234");
        adminUserRepository.save(admin);
    }
}
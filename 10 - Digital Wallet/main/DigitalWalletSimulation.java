package main;

import controller.AdminController;
import controller.TransactionController;
import controller.WalletController;
import domain.Transaction;
import domain.TransactionStatus;
import domain.User;
import repository.TransactionRepository;
import repository.UserRepository;
import repository.WalletRepository;
import repository.impl.TransactionRepositoryImpl;
import repository.impl.UserRepositoryImpl;
import repository.impl.WalletRepositoryImpl;
import service.LockService;
import service.TransactionService;
import service.WalletService;
import service.gateway.MockPaymentGatewayProvider;
import service.gateway.PaymentGatewayRouter;
import service.notification.EmailNotificationChannel;
import service.notification.NotificationMessage;
import service.notification.NotificationRouter;
import service.notification.SmsNotificationChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DigitalWalletSimulation {
    public static void main(String[] args) {
        System.out.println("=== DIGITAL WALLET SYSTEM SIMULATION ===\n");

        // Initialize repositories
        UserRepository userRepository = new UserRepositoryImpl();
        WalletRepository walletRepository = new WalletRepositoryImpl();
        TransactionRepository transactionRepository = new TransactionRepositoryImpl();

        // Initialize infrastructure services
        LockService lockService = new LockService(); // TODO: Replace with Redis-based distributed lock

        PaymentGatewayRouter paymentGatewayRouter = new PaymentGatewayRouter();
        paymentGatewayRouter.register(new MockPaymentGatewayProvider()); // TODO: Register real PG provider(s)

        NotificationRouter notificationRouter = new NotificationRouter();
        notificationRouter.register("email", new EmailNotificationChannel()); // TODO: Replace with real email provider
        notificationRouter.register("sms", new SmsNotificationChannel());     // TODO: Replace with Twilio

        // Initialize domain services
        WalletService walletService = new WalletService(walletRepository);
        TransactionService transactionService = new TransactionService(
                transactionRepository, walletRepository, lockService, paymentGatewayRouter, notificationRouter
        );

        // Initialize controllers
        WalletController walletController = new WalletController(walletService, transactionService);
        TransactionController transactionController = new TransactionController(transactionService);
        AdminController adminController = new AdminController(walletService);

        // Simulation flow
        System.out.println("1. Creating users...");
        User userA = new User(UUID.randomUUID().toString(), "alice", "alice@example.com", "Alice", System.currentTimeMillis());
        userRepository.save(userA);
        User userB = new User(UUID.randomUUID().toString(), "bob", "bob@example.com", "Bob", System.currentTimeMillis());
        userRepository.save(userB);
        System.out.println("Users created: " + userA + " | " + userB);

        System.out.println("\n2. Creating wallets...");
        var walletA = walletController.createWallet(userA.getId());
        var walletB = walletController.createWallet(userB.getId());
        System.out.println("Wallet A: " + walletA);
        System.out.println("Wallet B: " + walletB);

        System.out.println("\n3. Initiating deposit for Wallet A...");
        Map<String, String> paymentDetails = new HashMap<>();
        // TODO: populate card/UPI details as needed
        Transaction depTx = transactionController.initiateDeposit(walletA.getAccountNumber(), 50000, "CARD", "mock", paymentDetails);
        System.out.println("Deposit initiated: " + depTx);

        System.out.println("\n4. Simulating payment success callback...");
        transactionController.handlePaymentCallback(depTx.getProviderRef(), TransactionStatus.COMPLETED);
        System.out.println("Deposit completed. Balance A: " + walletController.getBalance(walletA.getAccountNumber()));

        System.out.println("\n5. Transfer from Wallet A to Wallet B...");
        Transaction transferTx = transactionController.transfer(walletA.getAccountNumber(), walletB.getAccountNumber(), 20000, "Pay Bob");
        System.out.println("Transfer completed: " + transferTx);
        System.out.println("Balance A: " + walletController.getBalance(walletA.getAccountNumber()));
        System.out.println("Balance B: " + walletController.getBalance(walletB.getAccountNumber()));

        System.out.println("\n6. Withdrawal request from Wallet B...");
        Transaction withdrawTx = transactionController.withdraw(walletB.getAccountNumber(), 10000, "Withdraw to bank");
        System.out.println("Withdrawal initiated: " + withdrawTx);

        System.out.println("\n7. Statement for Wallet A...");
        var statementA = walletController.getStatement(walletA.getAccountNumber(), null, null);
        System.out.println("Statement A: tx count = " + statementA.getTransactions().size() + ", balance = " + statementA.getCurrentBalanceMinor());

        System.out.println("\n8. Suspend Wallet B...");
        adminController.suspendWallet(walletB.getAccountNumber());
        System.out.println("Wallet B suspended. Trying another transfer (should fail)...");
        try {
            transactionController.transfer(walletA.getAccountNumber(), walletB.getAccountNumber(), 1000, "Test after suspend");
        } catch (Exception ex) {
            System.out.println("Expected failure: " + ex.getMessage());
        }

        System.out.println("\n=== SIMULATION COMPLETED ===");
    }
}


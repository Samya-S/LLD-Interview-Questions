package main;

import domain.*;
import domain.state.*;
import repository.*;
import service.*;
import controller.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VendingMachineSimulation {
    public static void main(String[] args) {
        System.out.println("=== Vending Machine System Simulation ===");
        
        // Initialize repositories
        VendingMachineRepository vendingMachineRepository = new VendingMachineRepository();
        ProductRepository productRepository = new ProductRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        RecoveryRepository recoveryRepository = new RecoveryRepository();
        
        // Initialize services
        VendingMachineService vendingMachineService = new VendingMachineService(vendingMachineRepository, productRepository);
        PaymentService paymentService = new PaymentService(vendingMachineRepository, paymentRepository);
        RecoveryService recoveryService = new RecoveryService(vendingMachineRepository, recoveryRepository, paymentRepository);
        AdminService adminService = new AdminService(vendingMachineRepository, productRepository, paymentRepository);
        
        // Initialize controllers
        VendingMachineController vendingMachineController = new VendingMachineController(vendingMachineService);
        PaymentController paymentController = new PaymentController(paymentService);
        RecoveryController recoveryController = new RecoveryController(recoveryService);
        AdminController adminController = new AdminController(adminService, vendingMachineService, 
                                                           vendingMachineRepository, 
                                                           productRepository);
        
        // Create a vending machine
        System.out.println("\n--- Creating Vending Machine ---");
        VendingMachine machine = new VendingMachine(1, "Main Lobby");
        vendingMachineRepository.save(machine);
        
        // Demonstrate system startup recovery
        System.out.println("\n--- System Startup Recovery Check ---");
        recoveryController.checkAndRecover();
        
        // Add some products to inventory
        System.out.println("\n--- Adding Products to Inventory ---");
        Product cola = new Product(1, "Cola", 2.50, ProductCategory.BEVERAGE);
        Product chips = new Product(2, "Chips", 1.50, ProductCategory.CHIPS);
        Product chocolate = new Product(3, "Chocolate", 1.00, ProductCategory.CANDY);
        Product water = new Product(4, "Water", 1.00, ProductCategory.BEVERAGE);
        Product cookies = new Product(5, "Cookies", 2.00, ProductCategory.COOKIES);
        
        productRepository.save(cola);
        productRepository.save(chips);
        productRepository.save(chocolate);
        productRepository.save(water);
        productRepository.save(cookies);
        
        machine.addProduct(cola, 10);
        machine.addProduct(chips, 15);
        machine.addProduct(chocolate, 20);
        machine.addProduct(water, 25);
        machine.addProduct(cookies, 12);
        
        System.out.println("✓ Added products to inventory");
        
        // Display available products
        System.out.println("\n--- Available Products ---");
        var products = vendingMachineController.getAvailableProducts(1);
        for (Product product : products) {
            System.out.println("  ✓ " + product.getName() + " - $" + product.getPrice());
        }
        
        // Process a payment
        System.out.println("\n--- Processing Payment ---");
        Map<Denomination, Integer> payment = new HashMap<>();
        payment.put(Denomination.FIVE_DOLLAR, 1);
        
        PaymentRequest paymentRequest = new PaymentRequest(1, 1, payment); // Buy 1 Cola
        Transaction transaction = paymentController.processPayment(1, paymentRequest);
        
        if (transaction != null) {
            System.out.println("✓ Payment processed successfully");
            System.out.println("  Transaction ID: " + transaction.getId());
            System.out.println("  Amount Required: $" + transaction.getAmountRequired());
            System.out.println("  Amount Inserted: $" + transaction.getAmountInserted());
            System.out.println("  Status: " + transaction.getStatus());
        }
        
        // Display machine state
        System.out.println("\n--- Machine State ---");
        System.out.println("  Current State: " + machine.getCurrentStateName());
        System.out.println("  Operational: " + machine.isOperational());
        
        // Wait for state transitions to complete and show final state
        System.out.println("\n--- State Transition Demonstration ---");
        try {
            Thread.sleep(2000); // Wait for payment processing and dispensing to complete
            System.out.println("  Final State after payment processing: " + machine.getCurrentStateName());
        } catch (InterruptedException e) {
            System.out.println("State transition demonstration interrupted");
        }
        
        // Display inventory status
        System.out.println("\n--- Inventory Status ---");
        adminController.displayInventoryStatus(1);
        
        // Display cash box status
        System.out.println("\n--- Cash Box Status ---");
        double totalCash = paymentController.getTotalCashInMachine(1);
        System.out.println("Total Cash in Machine: $" + totalCash);
        
        Map<Denomination, Integer> cashBox = paymentController.getCashBoxStatus(1);
        System.out.println("Denominations Available:");
        for (Map.Entry<Denomination, Integer> entry : cashBox.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " notes");
        }
        
        // Admin operations
        System.out.println("\n--- Admin Operations ---");
        adminController.displaySystemStatus(1);
        
        // Restock a product
        System.out.println("\n--- Restocking Product ---");
        adminController.restockProduct(1, 1, 5); // Add 5 more Colas
        
        // Collect cash
        System.out.println("\n--- Collecting Cash ---");
        adminController.collectCash(1);
        
        // Generate sales report
        System.out.println("\n--- Generating Sales Report ---");
        Date startDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 24 hours ago
        Date endDate = new Date();
        adminController.getSalesReport(1, startDate, endDate);
        
        // Recovery operations
        System.out.println("\n--- Recovery Operations ---");
        RecoveryStatus recoveryStatus = recoveryController.getRecoveryStatus(1);
        System.out.println("Recovery Status: " + recoveryStatus);
        
        // Create a recovery entry (simulating power failure during payment)
        System.out.println("\n--- Simulating Power Failure Recovery ---");
        recoveryController.createRecoveryEntry(1, transaction.getId(), new ProcessingPaymentState());
        
        // Perform recovery
        recoveryController.checkAndRecover(1);
        
        // Display final inventory
        System.out.println("\n--- Final Inventory Status ---");
        adminController.displayInventoryStatus(1);
        
        // Display final cash box status
        System.out.println("\n--- Final Cash Box Status ---");
        totalCash = paymentController.getTotalCashInMachine(1);
        System.out.println("Total Cash in Machine: $" + totalCash);
        
        // Display admin help
        System.out.println("\n--- Admin Help ---");
        adminController.displayAdminHelp();
        
        System.out.println("\n=== Simulation Complete ===");
        System.out.println("\n✓ Vending Machine System - Complete Implementation");
        System.out.println("  ✓ Domain Layer - All entities and state pattern");
        System.out.println("  ✓ Repository Layer - Data access abstraction");
        System.out.println("  ✓ Service Layer - Business logic implementation");
        System.out.println("  ✓ Controller Layer - API endpoints and user interface");
        System.out.println("  ✓ State Pattern - Machine state management");
        System.out.println("  ✓ Payment Processing - Cash handling and change calculation");
        System.out.println("  ✓ Inventory Management - Product tracking and restocking");
        System.out.println("  ✓ Recovery System - Power failure handling");
        System.out.println("  ✓ Admin Operations - Restocking, cash collection, reporting");
    }
}

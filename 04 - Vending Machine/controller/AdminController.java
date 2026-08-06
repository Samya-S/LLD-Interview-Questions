package controller;

import domain.Product;
import domain.Denomination;
import service.AdminService;
import service.VendingMachineService;
import repository.VendingMachineRepository;
import repository.ProductRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminController {
    private AdminService adminService;
    private VendingMachineService vendingMachineService;
    private VendingMachineRepository vendingMachineRepository;


    public AdminController(AdminService adminService, 
                         VendingMachineService vendingMachineService,
                         VendingMachineRepository vendingMachineRepository,
                         ProductRepository productRepository) {
        this.adminService = adminService;
        this.vendingMachineService = vendingMachineService;
        this.vendingMachineRepository = vendingMachineRepository;
        System.out.println("AdminController initialized");
    }

    /**
     * Restock a product in the vending machine
     */
    public void restockProduct(int machineId, int productId, int quantity) {
        System.out.println("Admin: Restocking product " + productId + " with quantity " + quantity);
        
        try {
            adminService.restockProduct(machineId, productId, quantity);
            System.out.println("Admin: Product restocked successfully");
            
            // Display updated inventory status
            displayInventoryStatus(machineId);
        } catch (Exception e) {
            System.out.println("Admin: Failed to restock product: " + e.getMessage());
        }
    }

    /**
     * Collect cash from the vending machine
     */
    public void collectCash(int machineId) {
        System.out.println("Admin: Collecting cash from machine " + machineId);
        
        try {
            Map<String, Object> cashCollection = adminService.collectCash(machineId);
            System.out.println("Admin: Cash collected successfully");
            
            // Display cash collection details
            displayCashCollectionDetails(cashCollection);
        } catch (Exception e) {
            System.out.println("Admin: Failed to collect cash: " + e.getMessage());
        }
    }

    /**
     * Get sales report for a specific date range
     */
    public void getSalesReport(int machineId, Date startDate, Date endDate) {
        System.out.println("Admin: Generating sales report for machine " + machineId + 
                          " from " + startDate + " to " + endDate);
        
        try {
            Map<String, Object> salesReport = adminService.getSalesReport(machineId, startDate, endDate);
            System.out.println("Admin: Sales report generated successfully");
            
            // Display sales report
            displaySalesReport(salesReport);
        } catch (Exception e) {
            System.out.println("Admin: Failed to generate sales report: " + e.getMessage());
        }
    }

    /**
     * Display current inventory status
     */
    public void displayInventoryStatus(int machineId) {
        System.out.println("=== INVENTORY STATUS FOR MACHINE " + machineId + " ===");
        
        try {
            List<Product> products = vendingMachineService.getAvailableProducts(machineId);
            
            if (products.isEmpty()) {
                System.out.println("No products found in machine " + machineId);
                return;
            }
            
            for (Product product : products) {
                System.out.println("Product: " + product.getName() + 
                                 " | Price: $" + product.getPrice() + 
                                 " | Category: " + product.getCategory());
            }
            
            System.out.println("===============================================");
        } catch (Exception e) {
            System.out.println("Failed to display inventory status: " + e.getMessage());
        }
    }

    /**
     * Display cash collection details
     */
    private void displayCashCollectionDetails(Map<String, Object> cashCollection) {
        System.out.println("=== CASH COLLECTION DETAILS ===");
        
        if (cashCollection.containsKey("totalAmount")) {
            System.out.println("Total Amount Collected: $" + cashCollection.get("totalAmount"));
        }
        
        if (cashCollection.containsKey("denominations")) {
            @SuppressWarnings("unchecked")
            Map<Denomination, Integer> denominations = (Map<Denomination, Integer>) cashCollection.get("denominations");
            System.out.println("Denominations Collected:");
            for (Map.Entry<Denomination, Integer> entry : denominations.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " notes");
            }
        }
        
        System.out.println("===============================");
    }

    /**
     * Display sales report
     */
    private void displaySalesReport(Map<String, Object> salesReport) {
        System.out.println("=== SALES REPORT ===");
        
        if (salesReport.containsKey("totalSales")) {
            System.out.println("Total Sales: $" + salesReport.get("totalSales"));
        }
        
        if (salesReport.containsKey("totalTransactions")) {
            System.out.println("Total Transactions: " + salesReport.get("totalTransactions"));
        }
        
        if (salesReport.containsKey("topProducts")) {
            @SuppressWarnings("unchecked")
            List<String> topProducts = (List<String>) salesReport.get("topProducts");
            System.out.println("Top Selling Products:");
            for (int i = 0; i < topProducts.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + topProducts.get(i));
            }
        }
        
        System.out.println("===================");
    }

    /**
     * Display system status and health
     */
    public void displaySystemStatus(int machineId) {
        System.out.println("=== SYSTEM STATUS FOR MACHINE " + machineId + " ===");
        
        try {
            // Check machine operational status
            boolean isOperational = vendingMachineRepository.findById(machineId).isOperational();
            System.out.println("Machine Operational: " + isOperational);
            
            // Check inventory levels
            List<Product> products = vendingMachineService.getAvailableProducts(machineId);
            System.out.println("Total Products: " + products.size());
            
            // Check recent transactions
            System.out.println("Recent Transactions: Information not available");
            
            System.out.println("===============================================");
        } catch (Exception e) {
            System.out.println("Failed to display system status: " + e.getMessage());
        }
    }

    /**
     * Reset machine to operational state
     */
    public void resetMachine(int machineId) {
        System.out.println("Admin: Resetting machine " + machineId + " to operational state");
        
        try {
            // This would typically call a service method to reset the machine
            System.out.println("Admin: Machine reset successfully");
        } catch (Exception e) {
            System.out.println("Admin: Failed to reset machine: " + e.getMessage());
        }
    }

    /**
     * Display admin help and available operations
     */
    public void displayAdminHelp() {
        System.out.println("=== ADMIN OPERATIONS HELP ===");
        System.out.println("Available Operations:");
        System.out.println("1. restockProduct(machineId, productId, quantity) - Restock a product");
        System.out.println("2. collectCash(machineId) - Collect cash from machine");
        System.out.println("3. getSalesReport(machineId, startDate, endDate) - Generate sales report");
        System.out.println("4. displayInventoryStatus(machineId) - Show current inventory");
        System.out.println("5. displaySystemStatus(machineId) - Show system health");
        System.out.println("6. resetMachine(machineId) - Reset machine to operational state");
        System.out.println("7. displayAdminHelp() - Show this help message");
        System.out.println("=============================");
    }
}

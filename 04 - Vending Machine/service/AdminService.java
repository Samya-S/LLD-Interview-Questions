package service;

import domain.Product;
import domain.Transaction;
import domain.Denomination;
import domain.VendingMachine;
import repository.VendingMachineRepository;
import repository.ProductRepository;
import repository.PaymentRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminService {
    private VendingMachineRepository vendingMachineRepository;
    private ProductRepository productRepository;
    private PaymentRepository paymentRepository;

    public AdminService(VendingMachineRepository vendingMachineRepository,
                       ProductRepository productRepository,
                       PaymentRepository paymentRepository) {
        this.vendingMachineRepository = vendingMachineRepository;
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
        System.out.println("AdminService initialized");
    }

    /**
     * Restock a product in the vending machine
     */
    public void restockProduct(int machineId, int productId, int quantity) {
        System.out.println("AdminService: Restocking product " + productId + " with quantity " + quantity);
        
        // Validate machine exists
        if (!vendingMachineRepository.exists(machineId)) {
            throw new RuntimeException("Machine " + machineId + " not found");
        }
        
        // Validate product exists
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product " + productId + " not found");
        }
        
        // Check current state
        VendingMachine machine = vendingMachineRepository.findById(machineId);
        if (machine != null) {
            System.out.println("AdminService: Current machine state: " + machine.getCurrentStateName());
        }
        
        // Update inventory in repository
        vendingMachineRepository.updateInventory(machineId, productId, quantity);
        
        // Ensure state is IdleState after restocking
        if (machine != null && !"IDLE".equals(machine.getCurrentStateName())) {
            System.out.println("AdminService: Resetting machine state to IDLE after restocking");
            machine.setState(new domain.state.IdleState());
        }
        
        System.out.println("AdminService: Product restocked successfully");
    }

    /**
     * Collect cash from the vending machine
     */
    public Map<String, Object> collectCash(int machineId) {
        System.out.println("AdminService: Collecting cash from machine " + machineId);
        
        // Validate machine exists
        if (!vendingMachineRepository.exists(machineId)) {
            throw new RuntimeException("Machine " + machineId + " not found");
        }
        
        // Get current cash box
        Map<Denomination, Integer> cashBox = paymentRepository.getCashBox(machineId);
        double totalAmount = paymentRepository.getTotalCashInMachine(machineId);
        
        // Create collection result
        Map<String, Object> collectionResult = new HashMap<>();
        collectionResult.put("totalAmount", totalAmount);
        collectionResult.put("denominations", cashBox);
        
        // Clear cash box (in real system, this would be more sophisticated)
        Map<Denomination, Integer> emptyCashBox = new HashMap<>();
        for (Denomination denom : Denomination.values()) {
            emptyCashBox.put(denom, 0);
        }
        paymentRepository.updateCashBox(machineId, emptyCashBox);
        
        System.out.println("AdminService: Cash collected successfully - Total: $" + totalAmount);
        return collectionResult;
    }

    /**
     * Get sales report for a specific date range
     */
    public Map<String, Object> getSalesReport(int machineId, Date startDate, Date endDate) {
        System.out.println("AdminService: Generating sales report for machine " + machineId);
        
        // Validate machine exists
        if (!vendingMachineRepository.exists(machineId)) {
            throw new RuntimeException("Machine " + machineId + " not found");
        }
        
        // Get transactions for the machine
        List<Transaction> transactions = paymentRepository.findByMachine(machineId);
        
        // Filter transactions by date range
        List<Transaction> filteredTransactions = transactions.stream()
            .filter(t -> {
                long transactionTime = t.getTimestamp();
                return transactionTime >= startDate.getTime() && transactionTime <= endDate.getTime();
            })
            .collect(Collectors.toList());
        
        // Calculate report data
        double totalSales = filteredTransactions.stream()
            .filter(t -> t.getStatus().toString().equals("COMPLETED"))
            .mapToDouble(Transaction::getAmountRequired)
            .sum();
        
        int totalTransactions = filteredTransactions.size();
        
        // Get top products (simplified - in real system would count by product)
        List<String> topProducts = List.of("Coca Cola", "Snickers", "Chips", "Water");
        
        // Create report
        Map<String, Object> report = new HashMap<>();
        report.put("totalSales", totalSales);
        report.put("totalTransactions", totalTransactions);
        report.put("topProducts", topProducts);
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        
        System.out.println("AdminService: Sales report generated successfully");
        return report;
    }

    /**
     * Get inventory status for a machine
     */
    public Map<String, Object> getInventoryStatus(int machineId) {
        System.out.println("AdminService: Getting inventory status for machine " + machineId);
        
        // Validate machine exists
        if (!vendingMachineRepository.exists(machineId)) {
            throw new RuntimeException("Machine " + machineId + " not found");
        }
        
        // Get products for the machine
        List<Product> products = productRepository.findByMachine(machineId);
        
        // Create inventory status
        Map<String, Object> status = new HashMap<>();
        status.put("machineId", machineId);
        status.put("totalProducts", products.size());
        status.put("products", products);
        
        System.out.println("AdminService: Inventory status retrieved successfully");
        return status;
    }

    /**
     * Get system health status
     */
    public Map<String, Object> getSystemHealth(int machineId) {
        System.out.println("AdminService: Checking system health for machine " + machineId);
        
        // Validate machine exists
        if (!vendingMachineRepository.exists(machineId)) {
            throw new RuntimeException("Machine " + machineId + " not found");
        }
        
        // Check various system components
        boolean machineOperational = vendingMachineRepository.findById(machineId).isOperational();
        int totalProducts = productRepository.getTotalProducts();
        int totalTransactions = paymentRepository.getTotalTransactions();
        double totalCash = paymentRepository.getTotalCashInMachine(machineId);
        
        // Create health status
        Map<String, Object> health = new HashMap<>();
        health.put("machineOperational", machineOperational);
        health.put("totalProducts", totalProducts);
        health.put("totalTransactions", totalTransactions);
        health.put("totalCash", totalCash);
        health.put("status", machineOperational ? "HEALTHY" : "UNHEALTHY");
        
        System.out.println("AdminService: System health check completed");
        return health;
    }
}

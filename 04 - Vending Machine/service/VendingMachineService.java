package service;

import domain.VendingMachine;
import domain.Product;
import domain.ProductCategory;
import repository.VendingMachineRepository;
import repository.ProductRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class VendingMachineService {
    private VendingMachineRepository vendingMachineRepository;
    private ProductRepository productRepository;
    
    public VendingMachineService(VendingMachineRepository vendingMachineRepository, ProductRepository productRepository) {
        this.vendingMachineRepository = vendingMachineRepository;
        this.productRepository = productRepository;
        System.out.println("VendingMachineService initialized");
    }
    
    public List<Product> getAvailableProducts(int machineId) {
        System.out.println("VendingMachineService: Getting available products for machine " + machineId);
        
        // TODO: Implement actual logic to get products from inventory
        // For now, return sample products
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Cola", 2.50, ProductCategory.BEVERAGE));
        products.add(new Product(2, "Chips", 1.50, ProductCategory.CHIPS));
        products.add(new Product(3, "Chocolate", 1.00, ProductCategory.CANDY));
        products.add(new Product(4, "Water", 1.00, ProductCategory.BEVERAGE));
        products.add(new Product(5, "Cookies", 2.00, ProductCategory.COOKIES));
        
        System.out.println("VendingMachineService: Found " + products.size() + " available products");
        return products;
    }
    
    public Product getProductDetails(int machineId, int productId) {
        System.out.println("VendingMachineService: Getting product details for product " + productId + " in machine " + machineId);
        
        // TODO: Implement actual logic
        Product product = productRepository.findById(productId);
        if (product == null) {
            // Return a default product if not found
            product = new Product(productId, "Product " + productId, 2.50, ProductCategory.BEVERAGE);
        }
        
        System.out.println("VendingMachineService: Retrieved product: " + product.getName());
        return product;
    }
    
    public Map<String, Object> getInventoryStatus(int machineId) {
        System.out.println("VendingMachineService: Getting inventory status for machine " + machineId);
        
        // TODO: Implement actual inventory status logic
        Map<String, Object> status = new HashMap<>();
        status.put("machineId", machineId);
        status.put("totalProducts", 5);
        status.put("lowStockProducts", 1);
        status.put("outOfStockProducts", 0);
        
        System.out.println("VendingMachineService: Inventory status retrieved");
        return status;
    }
    
    public void updateInventory(int machineId, int productId, int quantity) {
        System.out.println("VendingMachineService: Updating inventory for machine " + machineId + 
                          ", product " + productId + ", quantity " + quantity);
        
        // TODO: Implement actual inventory update logic
        vendingMachineRepository.updateInventory(machineId, productId, quantity);
        
        System.out.println("VendingMachineService: Inventory updated successfully");
    }
    
    public boolean isProductAvailable(int machineId, int productId) {
        System.out.println("VendingMachineService: Checking product availability for product " + productId + " in machine " + machineId);
        
        // TODO: Implement actual availability check
        // For now, assume all products are available
        return true;
    }
    
    public int getProductStock(int machineId, int productId) {
        System.out.println("VendingMachineService: Getting stock for product " + productId + " in machine " + machineId);
        
        // TODO: Implement actual stock check
        // For now, return a default stock value
        return 10;
    }
}

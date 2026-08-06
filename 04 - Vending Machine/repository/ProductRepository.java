package repository;

import domain.Product;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ProductRepository {
    private Map<Integer, Product> products;
    private int nextProductId;

    public ProductRepository() {
        this.products = new HashMap<>();
        this.nextProductId = 1;
        System.out.println("ProductRepository initialized");
    }

    public Product findById(int productId) {
        Product product = products.get(productId);
        if (product == null) {
            System.out.println("Repository: Product not found with ID: " + productId);
        } else {
            System.out.println("Repository: Found product with ID: " + productId);
        }
        return product;
    }

    public List<Product> findByMachine(int machineId) {
        // For simplicity, return all products since we don't have machine-specific products
        List<Product> machineProducts = new ArrayList<>(products.values());
        System.out.println("Repository: Found " + machineProducts.size() + " products for machine " + machineId);
        return machineProducts;
    }

    public Product save(Product product) {
        if (product.getId() == 0) {
            // Create a new product with generated ID
            Product newProduct = new Product(nextProductId++, product.getName(), product.getPrice(), product.getCategory());
            products.put(newProduct.getId(), newProduct);
            System.out.println("Repository: Saved new product with ID: " + newProduct.getId());
            return newProduct;
        } else {
            products.put(product.getId(), product);
            System.out.println("Repository: Updated product with ID: " + product.getId());
            return product;
        }
    }

    public void delete(int productId) {
        Product removed = products.remove(productId);
        if (removed != null) {
            System.out.println("Repository: Deleted product with ID: " + productId);
        } else {
            System.out.println("Repository: Product not found for deletion: " + productId);
        }
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public int getTotalProducts() {
        return products.size();
    }
}

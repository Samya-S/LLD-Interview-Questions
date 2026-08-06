package domain;

public class Product {
    private int id;
    private String name;
    private double price;
    private ProductCategory category;
    
    public Product(int id, String name, double price, ProductCategory category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public ProductCategory getCategory() { return category; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(ProductCategory category) { this.category = category; }
    
    @Override
    public String toString() {
        return name + " - $" + price;
    }
}

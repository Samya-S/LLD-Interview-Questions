package domain;

public class AdminUser {
    private String id;
    private String name;
    private String pinHash;
    private boolean isActive;

    public AdminUser(String id, String name, String pinHash) {
        this.id = id;
        this.name = name;
        this.pinHash = pinHash;
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPinHash() { return pinHash; }
    public void setPinHash(String pinHash) { this.pinHash = pinHash; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}

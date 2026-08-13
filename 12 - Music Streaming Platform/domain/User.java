package domain;

public class User {
    private String id;
    private String username;
    private String email;
    private String name;
    private SubscriptionTier subscriptionTier;
    private long createdAt;

    public User() {
    }

    public User(String id, String username, String email, String name, SubscriptionTier subscriptionTier, long createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.subscriptionTier = subscriptionTier;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SubscriptionTier getSubscriptionTier() {
        return subscriptionTier;
    }

    public void setSubscriptionTier(SubscriptionTier subscriptionTier) {
        this.subscriptionTier = subscriptionTier;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", subscriptionTier=" + subscriptionTier +
                '}';
    }
}

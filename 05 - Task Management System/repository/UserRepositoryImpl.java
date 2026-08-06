package repository;

import domain.User;
import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {
    private Map<Integer, User> users = new HashMap<>();

    public UserRepositoryImpl() {
        // Pre-populate with some users for demo
        users.put(1, new User(1, "john_doe", "john@example.com", domain.UserRole.USER));
        users.put(2, new User(2, "jane_smith", "jane@example.com", domain.UserRole.ADMIN));
    }

    @Override
    public User findById(int userId) {
        return users.get(userId);
    }
}
package repository.impl;

import domain.User;
import repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepositoryImpl implements UserRepository {
    private Map<String, User> usersById = new ConcurrentHashMap<>();
    private Map<String, String> userIdByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersById.put(user.getId(), user);
        userIdByEmail.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String id = userIdByEmail.get(email);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(usersById.get(id));
    }
}

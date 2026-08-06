package repository;

import domain.User;

public interface UserRepository {
    User findById(int userId);
}


package org.cinema.repository;

import org.cinema.model.User;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    void save(User user);
    Optional<User> getById(int userId);
    Set<User> findAll();
    void update(User user);
    void delete(int userId);
    Optional<User> getByUsername(String username);
}


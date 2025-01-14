package org.cinema.repository;

import org.cinema.model.User;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {
    void save(User user);
    Optional<User> getById(long userId);
    Set<User> findAll();
    void update(User user);
    void delete(long userId);
    Optional<User> getByUsername(String username);
}


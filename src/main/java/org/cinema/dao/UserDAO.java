package org.cinema.dao;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.User;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserDAO extends BaseDao implements Repository<User>{

    @Override
    public void add(User user) {
        executeTransaction(session -> session.save(user));
        log.info("User {} added successfully.", user.getUsername());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(executeTransactionWithResult(session -> {
            User user = session.get(User.class, id);
            if (user == null) {
                log.warn("User with ID {} not found.", id);
            } else {
                log.info("User with ID {} retrieved successfully.", id);
            }
            return user;
        }));
    }


    @Override
    public List<User> getAll() {
        return executeTransactionWithResult(session -> {
            Query<User> query = session.createQuery("FROM User", User.class);
            List<User> users = query.list();

            if (users == null || users.isEmpty()) {
                log.warn("No users found in the database.");
                return Collections.emptyList();
            }

            log.info("{} users retrieved successfully.", users.size());
            return users;
        });
    }

    @Override
    public void update(User user) {
        executeTransaction(session -> {
            User existingUser = session.get(User.class, user.getId());
            if (existingUser != null) {
                session.merge(user);
                log.info("User with ID {} updated successfully.", user.getId());
            } else {
                log.warn("User with this ID doesn't exist.");
                throw new IllegalArgumentException("User with ID " + user.getId() + " does not exist.");
            }
        });
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                log.info("User with ID {} deleted successfully.", id);
            } else {
                log.warn("User with ID {} does not exist.", id);
                throw new IllegalArgumentException("User with ID " + id + " does not exist.");
            }
        });
    }
}

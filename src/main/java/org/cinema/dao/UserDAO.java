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
        Optional<User> existingUser = getByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with this username already exists!");
        }
        executeTransaction(session -> session.save(user));
        log.info("User {} successfully added.", user.getUsername());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(executeTransactionWithResult(session -> {
            User user = session.get(User.class, id);
            if (user == null) {
                log.warn("User with ID {} not found.", id);
            } else {
                log.info("User with ID {} successfully found.", id);
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
                log.warn("Users not found in the database.");
                return Collections.emptyList();
            }

            log.info("{} users successfully retrieved.", users.size());
            return users;
        });
    }

    @Override
    public void update(User user) {
        executeTransaction(session -> {
            User existingUser = session.get(User.class, user.getId());
            if (existingUser != null) {
                session.merge(user);
                log.info("User with ID {} successfully updated.", user.getId());
            } else {
                log.warn("User with such ID does not exist.");
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
                log.info("User with ID {} successfully deleted.", id);
            } else {
                log.warn("User with ID {} does not exist.", id);
                throw new IllegalArgumentException("User with ID " + id + " does not exist.");
            }
        });
    }

    public Optional<User> getByUsername(String username) {
        return executeTransactionWithResult(session -> {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        });
    }
}

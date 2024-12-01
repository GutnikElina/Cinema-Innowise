package org.cinema.dao;

import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.model.User;
import org.hibernate.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserDAO extends BaseDao implements Repository<User>{

    public UserDAO() {
        super(HibernateConfig.getSessionFactory());
    }

    @Override
    public void add(User user) {
        try {
            if (user == null || user.getUsername() == null) {
                throw new IllegalArgumentException("User or username cannot be null");
            }

            if (getByUsername(user.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Error! User with username " + user.getUsername() + " already exists. Try again.");
            }
            executeTransaction(session -> session.save(user));
            log.info("User [{}] successfully added.", user.getUsername());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while adding user {}: ", user != null ? user.getUsername() : "null");
            throw new RuntimeException("Unexpected error while adding user.", e);
        }
    }

    @Override
    public Optional<User> getById(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("ID must be a positive number.");
            }

            return Optional.ofNullable(executeTransactionWithResult(session -> {
                User user = session.get(User.class, id);
                if (user == null) {
                    log.warn("User with ID {} not found.", id);
                } else {
                    log.info("User with ID {} successfully retrieved.", id);
                }
                return user;
            }));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while retrieving user with ID {}: ", id);
            throw new RuntimeException("Unexpected error while retrieving user by ID.", e);
        }
    }

    @Override
    public List<User> getAll() {
        try {
            return executeTransactionWithResult(session -> {
                log.info("Retrieving all users...");
                List<User> users = session.createQuery("FROM User", User.class).list();

                if (users.isEmpty()) {
                    log.warn("No users found in the database.");
                    return Collections.emptyList();
                }

                log.info("{} users successfully retrieved.", users.size());
                return users;
            });
        } catch (Exception e) {
            log.error("Error while retrieving all users: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while retrieving all users.", e);
        }
    }

    @Override
    public void update(User user) {
        try {
            if (user == null || user.getId() <= 0) {
                throw new IllegalArgumentException("User or ID cannot be null or invalid.");
            }

            if (getByUsername(user.getUsername()).isPresent()) {
                throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists.");
            }

            executeTransaction(session -> {
                User existingUser = session.get(User.class, user.getId());
                if (existingUser == null) {
                    throw new IllegalArgumentException("User with ID " + user.getId() + " doesn't exist.");
                }

                session.merge(user);
                log.info("User with ID {} successfully updated.", user.getId());
            });
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while updating user {}: ", user != null ? user.getId() : "null");
            throw new RuntimeException("Unexpected error while updating user.", e);
        }
    }

    @Override
    public void delete(int id) {
        try {
            if (id <= 0) {
                throw new IllegalArgumentException("ID must be a positive number.");
            }

            executeTransaction(session -> {
                User user = session.get(User.class, id);
                if (user == null) {
                    throw new IllegalArgumentException("User with ID " + id + " doesn't exist.");
                }

                session.delete(user);
                log.info("User with ID {} successfully deleted.", id);
            });
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while deleting user with ID {}: ", id);
            throw new RuntimeException("Unexpected error while deleting user.", e);
        }
    }

    public Optional<User> getByUsername(String username) {
        try {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username cannot be null or blank.");
            }

            return executeTransactionWithResult(session -> {
                Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
                query.setParameter("username", username);
                Optional<User> result = query.uniqueResultOptional();

                if (result.isEmpty()) {
                    log.warn("User with username {} not found.", username);
                } else {
                    log.info("User with username {} successfully retrieved.", username);
                }
                return result;
            });
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error while retrieving user by username {}: ", username, e);
            throw new RuntimeException("Unexpected error while retrieving user by username.", e);
        }
    }
}

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
            throw new IllegalArgumentException("Пользователь с таким логином уже существует!");
        }
        executeTransaction(session -> session.save(user));
        log.info("Пользователь {} успешно добавлен.", user.getUsername());
    }

    @Override
    public Optional<User> getById(int id) {
        return Optional.ofNullable(executeTransactionWithResult(session -> {
            User user = session.get(User.class, id);
            if (user == null) {
                log.warn("Пользователь с ID {} не найден.", id);
            } else {
                log.info("Пользователь с ID {} успешно найден.", id);
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
                log.warn("Пользователи не найдены в базе данных.");
                return Collections.emptyList();
            }

            log.info("{} пользователей успешно извлечены.", users.size());
            return users;
        });
    }

    @Override
    public void update(User user) {
        executeTransaction(session -> {
            User existingUser = session.get(User.class, user.getId());
            if (existingUser != null) {
                session.merge(user);
                log.info("Пользователь с ID {} успешно обновлен.", user.getId());
            } else {
                log.warn("Пользователь с таким ID не существует.");
                throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " не существует.");
            }
        });
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                log.info("Пользователь с ID {} успешно удален.", id);
            } else {
                log.warn("Пользователь с ID {} не существует.", id);
                throw new IllegalArgumentException("Пользователь с ID " + id + " не существует.");
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

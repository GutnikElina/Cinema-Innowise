package org.cinema.dao;

import java.util.List;
import java.util.Optional;

/**
 * Универсальный интерфейс для CRUD операций.
 *
 * @param <T> тип сущности
 */
public interface Repository<T> {
    void add(T entity);
    Optional<T> getById(int id);
    List<T> getAll();
    void update(T entity);
    void delete(int id);
}


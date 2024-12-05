package org.cinema.dao;

import java.util.List;
import java.util.Optional;

/**
 * A generic interface for CRUD operations.
 *
 * @param <T> the type of the entity
 */
public interface Repository<T> {
    void add(T entity);
    Optional<T> getById(int id);
    List<T> getAll();
    void update(T entity);
    void delete(int id);
}


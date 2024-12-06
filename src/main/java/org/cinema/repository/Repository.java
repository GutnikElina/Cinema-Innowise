package org.cinema.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    void save(T entity);
    Optional<T> getById(int id);
    List<T> findAll();
    void update(T entity);
    void delete(int id);
}


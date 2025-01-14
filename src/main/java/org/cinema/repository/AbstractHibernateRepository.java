package org.cinema.repository;

import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.hibernate.SessionFactory;

import java.util.Optional;

@Slf4j
public abstract class AbstractHibernateRepository<T> extends BaseRepository {

    protected Class<T> entityClass;

    protected AbstractHibernateRepository(SessionFactory sessionFactory, Class<T> entityClass) {
        super(sessionFactory);
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        executeTransaction(session -> session.save(entity));
    }

    public void update(T entity) {
        executeTransaction(session -> session.update(entity));
    }

    public void delete(long id) {
        executeTransaction(session -> {
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.delete(entity);
             } else {
                throw new NoDataFoundException("Entity with ID '" + id + "' not found.");
            }
        });
    }

    public Optional<T> getById(long id) {
        return Optional.ofNullable(executeWithResult(session ->
                session.get(entityClass, id)));
    }
}
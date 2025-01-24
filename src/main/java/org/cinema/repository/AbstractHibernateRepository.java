package org.cinema.repository;

import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.hibernate.SessionFactory;

import java.util.Optional;

/**
 * Abstract repository class for basic Hibernate operations on entities of type {@link T}.
 * This class provides methods for saving, updating, deleting, and retrieving entities from the database.
 *
 * @param <T> the type of the entity that the repository handles.
 */
@Slf4j
public abstract class AbstractHibernateRepository<T> extends BaseRepository {

    protected Class<T> entityClass;

    /**
     * Constructor for initializing the repository with a session factory and entity class.
     *
     * @param sessionFactory the Hibernate {@link SessionFactory} used for session management.
     * @param entityClass the {@link Class} of the entity that this repository works with.
     */
    protected AbstractHibernateRepository(SessionFactory sessionFactory, Class<T> entityClass) {
        super(sessionFactory);
        this.entityClass = entityClass;
    }

    /**
     * Saves the specified entity to the database.
     *
     * @param entity the entity to save.
     */
    public void save(T entity) {
        executeTransaction(session -> session.save(entity));
    }

    /**
     * Updates the specified entity in the database.
     *
     * @param entity the entity to update.
     */
    public void update(T entity) {
        executeTransaction(session -> session.update(entity));
    }

    /**
     * Deletes the entity with the specified ID from the database.
     * If no entity is found, a {@link NoDataFoundException} will be thrown.
     *
     * @param id the ID of the entity to delete.
     */
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

    /**
     * Retrieves an entity by its ID.
     * If the entity is not found, an empty {@link Optional} will be returned.
     *
     * @param id the ID of the entity to retrieve.
     * @return an {@link Optional} containing the entity if found, otherwise an empty {@link Optional}.
     */
    public Optional<T> getById(long id) {
        return Optional.ofNullable(executeWithResult(session ->
                session.get(entityClass, id)));
    }
}
package org.cinema.repository;

import org.cinema.model.FilmSession;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing {@link FilmSession} entities.
 * Provides methods for saving, retrieving, updating, and deleting film sessions,
 * as well as finding sessions by specific criteria.
 */
public interface SessionRepository {

    /**
     * Saves a new film session to the repository.
     *
     * @param filmSession the {@link FilmSession} entity to be saved.
     */
    void save(FilmSession filmSession);

    /**
     * Retrieves a film session by its unique identifier.
     *
     * @param filmSessionId the ID of the film session to retrieve.
     * @return an {@link Optional} containing the {@link FilmSession} if found, or empty if not found.
     */
    Optional<FilmSession> getById(long filmSessionId);

    /**
     * Retrieves all film sessions in the repository.
     *
     * @return a {@link Set} of all {@link FilmSession} entities.
     */
    Set<FilmSession> findAll();

    /**
     * Updates an existing film session in the repository.
     *
     * @param filmSession the {@link FilmSession} entity with updated details.
     */
    void update(FilmSession filmSession);

    /**
     * Deletes a film session from the repository by its unique identifier.
     *
     * @param filmSessionId the ID of the film session to delete.
     */
    void delete(long filmSessionId);

    /**
     * Checks if a film session with the same details already exists in the repository.
     *
     * @param filmSession the {@link FilmSession} entity to check.
     * @return {@code true} if the session exists, {@code false} otherwise.
     */
    boolean checkIfSessionExists(FilmSession filmSession);

    /**
     * Finds all film sessions scheduled for a specific date.
     *
     * @param date the {@link LocalDate} to search for film sessions.
     * @return a {@link Set} of {@link FilmSession} entities scheduled for the specified date.
     */
    Set<FilmSession> findByDate(LocalDate date);
}

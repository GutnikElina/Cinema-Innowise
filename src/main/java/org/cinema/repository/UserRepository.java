package org.cinema.repository;

import org.cinema.model.User;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing {@link User} entities.
 * Provides methods for saving, retrieving, updating, and deleting users,
 * as well as finding users by specific criteria.
 */
public interface UserRepository {

    /**
     * Saves a new user to the repository.
     *
     * @param user the {@link User} entity to be saved.
     */
    void save(User user);

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param userId the ID of the user to retrieve.
     * @return an {@link Optional} containing the {@link User} if found, or empty if not found.
     */
    Optional<User> getById(long userId);

    /**
     * Retrieves all users in the repository.
     *
     * @return a {@link Set} of all {@link User} entities.
     */
    Set<User> findAll();

    /**
     * Updates an existing user in the repository.
     *
     * @param user the {@link User} entity with updated details.
     */
    void update(User user);

    /**
     * Deletes a user from the repository by its unique identifier.
     *
     * @param userId the ID of the user to delete.
     */
    void delete(long userId);

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve.
     * @return an {@link Optional} containing the {@link User} if found, or empty if not found.
     */
    Optional<User> getByUsername(String username);
}

package org.cinema.repository;

import org.cinema.model.Movie;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing {@link Movie} entities.
 * Provides methods for saving, retrieving, updating, and deleting movies,
 * as well as finding movies by specific criteria.
 */
public interface MovieRepository {

    /**
     * Saves a new movie to the repository.
     *
     * @param movie the {@link Movie} entity to be saved.
     */
    void save(Movie movie);

    /**
     * Retrieves a movie by its unique identifier.
     *
     * @param movieId the ID of the movie to retrieve.
     * @return an {@link Optional} containing the {@link Movie} if found, or empty if not found.
     */
    Optional<Movie> getById(long movieId);

    /**
     * Retrieves all movies in the repository.
     *
     * @return a {@link List} of all {@link Movie} entities.
     */
    List<Movie> findAll();

    /**
     * Updates an existing movie in the repository.
     *
     * @param movie the {@link Movie} entity with updated details.
     */
    void update(Movie movie);

    /**
     * Deletes a movie from the repository by its unique identifier.
     *
     * @param movieId the ID of the movie to delete.
     */
    void delete(long movieId);

    /**
     * Finds movies with a title matching the specified string.
     *
     * @param movieTitle the title of the movie(s) to find.
     * @return a {@link List} of {@link Movie} entities matching the title.
     */
    List<Movie> findByTitle(String movieTitle);
}

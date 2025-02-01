package org.cinema.repository;

import org.cinema.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing {@link Movie} entities.
 * Provides methods for saving, retrieving, updating, and deleting movies,
 * as well as finding movies by specific criteria.
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    /**
     * Finds movies with a title containing the specified string, case-insensitive.
     *
     * @param title the title of the movie(s) to find.
     * @return a {@link List} of {@link Movie} entities matching the title.
     */
    List<Movie> findByTitleContainingIgnoreCase(String title);
}

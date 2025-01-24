package org.cinema.service;

import org.cinema.dto.movieDTO.MovieResponseDTO;
import java.util.List;

/**
 * Service interface for managing movies.
 */
public interface MovieService {
    /**
     * Retrieves a list of all movies.
     *
     * @return a list of {@link MovieResponseDTO}.
     */
    List<MovieResponseDTO> findAll();

    /**
     * Searches for movies by title.
     *
     * @param title the title of the movie to search for.
     * @return a list of matching {@link MovieResponseDTO}.
     */
    List<MovieResponseDTO> searchMovies(String title);
}

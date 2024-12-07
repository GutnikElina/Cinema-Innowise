package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Movie;
import org.cinema.service.MovieService;
import org.cinema.util.OmdbApiUtil;
import java.util.List;

@Slf4j
public class MovieServiceImpl implements MovieService {

    @Getter
    private static final MovieServiceImpl instance = new MovieServiceImpl();

    @Override
    public List<Movie> searchMovies(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title mustn't be null or empty.");
        }

        log.debug("Service layer: Initiating search for movies with title: {} ...", title);
        try {
            List<Movie> movies = OmdbApiUtil.searchMovies(title.trim());
            if (movies.isEmpty()) {
                log.warn("Error! No movies found for the title: {}", title);
                throw new IllegalArgumentException("Error! Please provide a valid movie title.");
            }
            return movies;
        } catch (Exception e) {
            log.error("Unexpected error in service layer while searching for movies: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while searching for movies", e);
        }
    }
}

package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.NoDataFoundException;
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

        List<Movie> movies = OmdbApiUtil.searchMovies(title.trim());

        if (movies.isEmpty()) {
            log.warn("Error! No movies found for the title: {}", title);
            throw new NoDataFoundException("Error! Please provide a valid movie title.");
        }
        return movies;
    }

    @Override
    public Movie getMovie(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Movie title mustn't be null or empty.");
        }

        return OmdbApiUtil.getMovie(title.trim());
    }
}

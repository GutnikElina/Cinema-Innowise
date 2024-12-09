package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.Movie;
import org.cinema.service.MovieService;
import org.cinema.util.OmdbApiUtil;
import org.cinema.util.ValidationUtil;

import java.util.List;

@Slf4j
public class MovieServiceImpl implements MovieService {

    @Getter
    private static final MovieServiceImpl instance = new MovieServiceImpl();

    @Override
    public List<Movie> searchMovies(String title) {
        ValidationUtil.validateMovieTitle(title);
        List<Movie> movies = OmdbApiUtil.searchMovies(title.trim());

        if (movies.isEmpty()) {
            log.warn("Error! No movies found for the title: {}", title);
            throw new NoDataFoundException("Error! No movies found for this title.");
        }
        return movies;
    }

    @Override
    public Movie getMovie(String title) {
        ValidationUtil.validateMovieTitle(title);
        return OmdbApiUtil.getMovie(title.trim());
    }
}

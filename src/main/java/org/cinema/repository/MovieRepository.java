package org.cinema.repository;

import org.cinema.model.Movie;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MovieRepository {
    void save(Movie movie);
    Optional<Movie> getById(int movieId);
    List<Movie> findAll();
    void update(Movie movie);
    void delete(int movieId);
    List<Movie> findByTitle(String movieTitle);
}

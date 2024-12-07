package org.cinema.service;

import org.cinema.model.Movie;
import java.util.List;

public interface MovieService {
    List<Movie> searchMovies(String title);
}

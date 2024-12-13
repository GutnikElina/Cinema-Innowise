package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.Movie;
import org.cinema.model.MovieAPI;
import org.cinema.repository.impl.MovieRepositoryImpl;
import org.cinema.service.MovieService;
import org.cinema.util.OmdbApiUtil;
import org.cinema.util.ValidationUtil;

import java.util.List;

@Slf4j
public class MovieServiceImpl implements MovieService {

    @Getter
    private static final MovieServiceImpl instance = new MovieServiceImpl();

    private final MovieRepositoryImpl movieRepository = MovieRepositoryImpl.getInstance();

    @Override
    public List<Movie> searchMovies(String title) {
        ValidationUtil.validateTitle(title);
        List<Movie> moviesFromDb = movieRepository.findByTitle(title);
        if (!moviesFromDb.isEmpty()) {
            log.info("Found {} movie(s) with title '{}'", moviesFromDb.size(), title);
            return moviesFromDb;
        }

        List<MovieAPI> apiMovies = OmdbApiUtil.searchMovies(title);
        return apiMovies.stream()
                .map(this::convertToMovie)
                .peek(this::saveMovieToDatabase)
                .toList();
    }

    @Override
    public Movie getMovie(String title) {
        ValidationUtil.validateTitle(title);
        List<Movie> moviesFromDb = movieRepository.findByTitle(title);
        if (!moviesFromDb.isEmpty()) {
            log.info("Returning the first found movie with title '{}'", title);
            return moviesFromDb.get(0);
        }

        List<MovieAPI> apiMovies = OmdbApiUtil.searchMovies(title);
        List<Movie> movies = apiMovies.stream()
                .map(this::convertToMovie)
                .peek(this::saveMovieToDatabase)
                .toList();

        Movie movie = movies.get(0);
        if (movie == null) {
            throw new NoDataFoundException("No movie found with title: " + title);
        }

        return movie;
    }

    private void saveMovieToDatabase(Movie movie) {
        movieRepository.save(movie);
        log.info("Saved movie '{}' to database", movie.getTitle());
    }

    private Movie convertToMovie(MovieAPI apiMovie) {
        Movie movie = new Movie();
        movie.setTitle(apiMovie.getTitle());
        movie.setYear(apiMovie.getYear());
        movie.setPoster(apiMovie.getPoster());
        movie.setPlot(apiMovie.getPlot());
        movie.setGenre(apiMovie.getGenre());
        movie.setDirector(apiMovie.getDirector());
        movie.setActors(apiMovie.getActors());
        movie.setImdbRating(apiMovie.getImdbRating());
        movie.setRuntime(apiMovie.getRuntime());
        return movie;
    }
}

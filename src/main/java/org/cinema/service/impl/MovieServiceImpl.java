package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.mapper.filmSessionMapper.FilmSessionResponseMapper;
import org.cinema.mapper.movieMapper.MovieResponseMapper;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.model.MovieAPI;
import org.cinema.repository.impl.MovieRepositoryImpl;
import org.cinema.service.MovieService;
import org.cinema.util.OmdbApiUtil;
import org.cinema.util.ValidationUtil;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class MovieServiceImpl implements MovieService {

    @Getter
    private static final MovieServiceImpl instance = new MovieServiceImpl();

    private final MovieRepositoryImpl movieRepository = MovieRepositoryImpl.getInstance(HibernateConfig.getSessionFactory());

    @Override
    public List<MovieResponseDTO> findAll() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(MovieResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponseDTO> searchMovies(String title) {
        ValidationUtil.validateTitle(title);
        List<Movie> moviesFromDb = movieRepository.findByTitle(title);
        if (!moviesFromDb.isEmpty()) {
            log.info("Found {} movie(s) with title '{}'", moviesFromDb.size(), title);
            return moviesFromDb.stream()
                    .map(MovieResponseMapper.INSTANCE::toDTO)
                    .toList();
        }

        List<MovieAPI> apiMovies = OmdbApiUtil.searchMovies(title);
        return apiMovies.stream()
                .map(this::convertToMovie)
                .peek(this::saveMovieToDatabase)
                .map(MovieResponseMapper.INSTANCE::toDTO)
                .toList();
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
        movie.setImdbRating(apiMovie.getImdbRating());
        movie.setRuntime(apiMovie.getRuntime());
        return movie;
    }
}

package org.cinema.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.mapper.movieMapper.MovieApiCreateMapper;
import org.cinema.mapper.movieMapper.MovieResponseMapper;
import org.cinema.model.Movie;
import org.cinema.model.MovieAPI;
import org.cinema.repository.MovieRepository;
import org.cinema.service.MovieService;
import org.cinema.util.OmdbApiUtil;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<MovieResponseDTO> findAll() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream()
                .map(MovieResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieResponseDTO> searchMovies(String title) {
        ValidationUtil.validateNotBlank(title, "movie_title");

        List<Movie> moviesFromDb = movieRepository.findByTitleContainingIgnoreCase(title);
        if (!moviesFromDb.isEmpty()) {
            log.info("Found {} movie(s) with title '{}'", moviesFromDb.size(), title);
            return moviesFromDb.stream()
                    .map(MovieResponseMapper.INSTANCE::toDTO)
                    .toList();
        }

        List<MovieAPI> apiMovies = OmdbApiUtil.searchMovies(title);
        return apiMovies.stream()
                .map(MovieApiCreateMapper.INSTANCE::toEntity)
                .peek(this::saveMovieToDatabase)
                .map(MovieResponseMapper.INSTANCE::toDTO)
                .toList();
    }

    private void saveMovieToDatabase(Movie movie) {
        movieRepository.save(movie);
        log.debug("Saved movie '{}' to database", movie.getTitle());
    }
}
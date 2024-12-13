package org.cinema.repository.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.Movie;
import org.cinema.repository.BaseRepository;
import org.cinema.repository.MovieRepository;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MovieRepositoryImpl extends BaseRepository implements MovieRepository {

    @Getter
    private static final MovieRepositoryImpl instance = new MovieRepositoryImpl();

    public MovieRepositoryImpl() {
        super(HibernateConfig.getSessionFactory());
    }

    @Override
    public void save(Movie movie) {
        executeTransaction(session ->
                session.save(movie));
        log.info("Movie '{}' successfully added.", movie.getTitle());
    }

    @Override
    public Optional<Movie> getById(int movieId) {
        return Optional.ofNullable(executeWithResult(session ->
                session.get(Movie.class, movieId)));
    }

    @Override
    public List<Movie> findAll() {
        return executeWithResult(session -> {
            log.debug("Retrieving all movies...");
            List<Movie> movies = session.createQuery("FROM Movie", Movie.class).list();

            log.info("{} movies successfully retrieved.", movies.size());
            return movies;
        });
    }

    @Override
    public void update(Movie movie) {
        executeTransaction(session -> {
            session.merge(movie);
            log.info("Movie with title '{}' successfully updated.", movie.getTitle());
        });
    }

    @Override
    public void delete(int movieId) {
        executeTransaction(session -> {
            Movie movie = session.get(Movie.class, movieId);
            if (movie == null) {
                throw new NoDataFoundException("Error! Movie with title " + movieId + " doesn't exist.");
            }
            session.delete(movie);
            log.info("Movie with title '{}' successfully deleted.", movie.getTitle());
        });
    }

    @Override
    public List<Movie> findByTitle(String title) {
        return executeWithResult(session -> {
            Query<Movie> query = session.createQuery(
                    "FROM Movie WHERE LOWER(title) LIKE LOWER(:title)", Movie.class);
            query.setParameter("title", "%" + title + "%");
            return query.list();
        });
    }
}
package org.cinema.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Movie;
import org.cinema.repository.AbstractHibernateRepository;
import org.cinema.repository.MovieRepository;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MovieRepositoryImpl extends AbstractHibernateRepository<Movie> implements MovieRepository {

    private MovieRepositoryImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Movie.class);
    }

    public static MovieRepositoryImpl getInstance(SessionFactory sessionFactory) {
        Holder.initialize(sessionFactory);
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static MovieRepositoryImpl INSTANCE;

        static void initialize(SessionFactory sessionFactory) {
            if (INSTANCE == null) {
                INSTANCE = new MovieRepositoryImpl(sessionFactory);
            }
        }
    }

    @Override
    public void save(Movie movie) {
        super.save(movie);
        log.info("Movie '{}' successfully added.", movie.getTitle());
    }

    @Override
    public void update(Movie movie) {
        super.update(movie);
        log.info("Movie with title '{}' successfully updated.", movie.getTitle());
    }

    @Override
    public void delete(long movieId) {
        super.delete(movieId);
    }

    @Override
    public Optional<Movie> getById(long movieId) {
        return super.getById(movieId);
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
    public List<Movie> findByTitle(String title) {
        return executeWithResult(session -> {
            Query<Movie> query = session.createQuery("FROM Movie WHERE LOWER(title) LIKE LOWER(:title)", Movie.class);
            query.setParameter("title", "%" + title + "%");
            return query.list();
        });
    }
}
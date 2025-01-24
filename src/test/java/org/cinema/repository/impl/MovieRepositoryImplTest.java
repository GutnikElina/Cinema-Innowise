package org.cinema.repository.impl;

import org.cinema.config.TestHibernateConfig;
import org.cinema.model.Movie;
import org.cinema.repository.MovieRepository;
import org.junit.jupiter.api.*;
import org.hibernate.SessionFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MovieRepositoryImplTest {

    private static SessionFactory sessionFactory;
    private static MovieRepository movieRepository;

    @BeforeAll
    public static void setUp() {
        sessionFactory = TestHibernateConfig.getSessionFactory();
        movieRepository = MovieRepositoryImpl.getInstance(sessionFactory);
    }

    @AfterAll
    public static void tearDown() {
        TestHibernateConfig.closeSessionFactory();
    }

    @BeforeEach
    public void init() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Movie").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    public void testSaveMovie() {
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setGenre("Drama");

        movieRepository.save(movie);

        Movie retrievedMovie = movieRepository.getById(movie.getId()).orElse(null);
        assertNotNull(retrievedMovie);
        assertEquals("Test Movie", retrievedMovie.getTitle());
    }

    @Test
    public void testFindAllMovies() {
        Movie movie1 = new Movie();
        movie1.setTitle("Movie 1");
        movie1.setGenre("Action");
        movieRepository.save(movie1);

        Movie movie2 = new Movie();
        movie2.setTitle("Movie 2");
        movie2.setGenre("Comedy");
        movieRepository.save(movie2);

        List<Movie> movies = movieRepository.findAll();
        assertNotNull(movies);
        assertEquals(2, movies.size());
    }

    @Test
    public void testFindMovieByTitle() {
        Movie movie = new Movie();
        movie.setTitle("Unique Movie");
        movie.setGenre("Horror");
        movieRepository.save(movie);

        List<Movie> foundMovies = movieRepository.findByTitle("Unique Movie");
        assertNotNull(foundMovies);
        assertEquals(1, foundMovies.size());
        assertEquals("Unique Movie", foundMovies.get(0).getTitle());
    }

    @Test
    public void testUpdateMovie() {
        Movie movie = new Movie();
        movie.setTitle("Old Title");
        movie.setGenre("Drama");
        movieRepository.save(movie);

        movie.setTitle("Updated Title");
        movieRepository.update(movie);

        Movie updatedMovie = movieRepository.getById(movie.getId()).orElse(null);
        assertNotNull(updatedMovie);
        assertEquals("Updated Title", updatedMovie.getTitle());
    }

    @Test
    public void testDeleteMovie() {
        Movie movie = new Movie();
        movie.setTitle("To Be Deleted");
        movie.setGenre("Comedy");
        movieRepository.save(movie);

        long movieId = movie.getId();
        movieRepository.delete(movieId);

        Movie deletedMovie = movieRepository.getById(movieId).orElse(null);
        assertNull(deletedMovie);
    }
}

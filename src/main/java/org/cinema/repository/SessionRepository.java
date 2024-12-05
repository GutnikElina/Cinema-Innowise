package org.cinema.repository;

import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.model.FilmSession;
import org.hibernate.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SessionRepository extends BaseRepository implements Repository<FilmSession> {

    public SessionRepository() {
        super(HibernateConfig.getSessionFactory());
    }

    @Override
    public void add(FilmSession filmSession) {
        try {
            if (checkIfSessionExists(filmSession)) {
                String errorMessage = "Film session already exists on this film and time. Try again.";
                log.warn("Occurred error while adding film session: {}", errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            executeTransaction(session -> session.save(filmSession));
            log.info("Film session [{}] successfully added.", filmSession);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding film session: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while adding film session.", e);
        }
    }

    @Override
    public Optional<FilmSession> getById(int id) {
        try {
            return Optional.ofNullable(executeWithResult(session -> {
                FilmSession filmSession = session.get(FilmSession.class, id);
                if (filmSession == null) {
                    log.warn("Film session with ID {} not found.", id);
                } else {
                    log.info("Film session with ID {} successfully found.", id);
                }
                return filmSession;
            }));
        } catch (Exception e) {
            log.error("Unexpected error while retrieving film session by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Unexpected error while retrieving film session by ID.", e);
        }
    }

    @Override
    public List<FilmSession> getAll() {
        try {
            return executeWithResult(session -> {
                List<FilmSession> filmSessions = session.createQuery("FROM FilmSession", FilmSession.class).list();
                if (filmSessions.isEmpty()) {
                    log.warn("No film sessions found in the database.");
                    return Collections.emptyList();
                }
                log.info("{} film sessions successfully retrieved.", filmSessions.size());
                return filmSessions;
            });
        } catch (Exception e) {
            log.error("Unexpected error while retrieving film sessions: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while retrieving film sessions.", e);
        }
    }

    @Override
    public void update(FilmSession filmSession) {
        try {
            if (checkIfSessionExists(filmSession)) {
                String errorMessage = "Film session already exists on this film and time. Try again.";
                log.warn("Occurred error while updating film session: {}", errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            executeTransaction(session -> {
                FilmSession existingFilmSession = session.get(FilmSession.class, filmSession.getId());
                if (existingFilmSession == null) {
                    String errorMessage = "Film session with ID " + filmSession.getId() + " doesn't exist.";
                    log.warn("Occurred error while updating film session: {}", errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }

                session.merge(filmSession);
                log.info("Film session with ID [{}] successfully updated.", filmSession.getId());
            });
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating film session with ID {}: {}", filmSession.getId(), e.getMessage());
            throw new RuntimeException("Unexpected error while updating film session.", e);
        }
    }

    @Override
    public void delete(int id) {
        try {
            executeTransaction(session -> {
                FilmSession filmSession = session.get(FilmSession.class, id);
                if (filmSession == null) {
                    String errorMessage = "Film session with ID " + id + " doesn't exist.";
                    log.warn(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
                session.delete(filmSession);
                log.info("Film session with ID {} successfully deleted.", id);
            });
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting film session with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Unexpected error while deleting film session.", e);
        }
    }

    private boolean checkIfSessionExists(FilmSession filmSession) {
        try {
            return executeWithResult(session -> {
                Query<FilmSession> query = session.createQuery(
                        "FROM FilmSession fs WHERE fs.movieTitle = :movieTitle " +
                                "AND fs.date = :date " +
                                "AND fs.startTime = :startTime", FilmSession.class);
                query.setParameter("movieTitle", filmSession.getMovieTitle());
                query.setParameter("date", filmSession.getDate());
                query.setParameter("startTime", filmSession.getStartTime());

                boolean exists = !query.list().isEmpty();
                log.debug("Check for existing session with title '{}', date '{}', start time '{}': {}.",
                        filmSession.getMovieTitle(), filmSession.getDate(), filmSession.getStartTime(),
                        exists ? "found" : "not found");
                return exists;
            });
        } catch (Exception e) {
            log.error("Unexpected error while checking for existing session with title '{}', date '{}', start time '{}': {}",
                    filmSession.getMovieTitle(), filmSession.getDate(), filmSession.getStartTime(), e.getMessage());
            throw new RuntimeException("Unexpected error while checking if film session exists.", e);
        }
    }
}
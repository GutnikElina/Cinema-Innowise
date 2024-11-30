package org.cinema.dao;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.FilmSession;
import org.hibernate.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SessionDAO extends BaseDao implements Repository<FilmSession>{

    @Override
    public void add(FilmSession filmSession) {
        boolean sessionExists = checkIfSessionExists(filmSession);
        if (sessionExists) {
            log.warn("Film session already exists with the same movie, date, and time.");
            throw new IllegalArgumentException("Session with the same movie, date, and time already exists.");
        }
        executeTransaction(session -> session.save(filmSession));
        log.info("Film session successfully added.");
    }

    @Override
    public Optional<FilmSession> getById(int id) {
        return Optional.ofNullable(executeTransactionWithResult(session -> {
            FilmSession filmSession = session.get(FilmSession.class, id);
            if (filmSession == null) {
                log.warn("Film session with ID {} not found.", id);
            } else {
                log.info("Film session with ID {} successfully found.", id);
            }
            return filmSession;
        }));
    }

    @Override
    public List<FilmSession> getAll() {
        return executeTransactionWithResult(session -> {
            Query<FilmSession> query = session.createQuery("FROM FilmSession", FilmSession.class);
            List<FilmSession> filmSessions = query.list();

            if (filmSessions == null || filmSessions.isEmpty()) {
                log.warn("No film sessions found in the database.");
                return Collections.emptyList();
            }
            log.info("{} film sessions successfully retrieved.", filmSessions.size());
            return filmSessions;
        });
    }

    @Override
    public void update(FilmSession filmSession) {
        boolean sessionExists = checkIfSessionExists(filmSession);
        if (sessionExists) {
            log.warn("Film session already exists with the same movie, date, and start time.");
            throw new IllegalArgumentException("Session with the same movie, date, and time already exists.");
        }

        executeTransaction(session -> {
            FilmSession existingFilmSession = session.get(FilmSession.class, filmSession.getId());
            if (existingFilmSession != null) {
                session.merge(filmSession);
                log.info("Film session with ID {} successfully updated.", filmSession.getId());
            } else {
                log.warn("Film session with such ID does not exist.");
                throw new IllegalArgumentException("Film session with ID " + filmSession.getId() + " does not exist.");
            }
        });
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            FilmSession filmSession = session.get(FilmSession.class, id);
            if (filmSession != null) {
                session.delete(filmSession);
                log.info("Film session with ID {} successfully deleted.", id);
            } else {
                log.warn("Film session with ID {} does not exist.", id);
                throw new IllegalArgumentException("Film session with ID " + id + " does not exist.");
            }
        });
    }

    private boolean checkIfSessionExists(FilmSession filmSession) {
        return executeTransactionWithResult(session -> {
            Query<FilmSession> query = session.createQuery(
                    "FROM FilmSession fs WHERE fs.movieTitle = :movieTitle " +
                            "AND fs.date = :date " +
                            "AND fs.startTime = :startTime", FilmSession.class);
            query.setParameter("movieTitle", filmSession.getMovieTitle());
            query.setParameter("date", filmSession.getDate());
            query.setParameter("startTime", filmSession.getStartTime());
            List<FilmSession> result = query.list();
            return !result.isEmpty();
        });
    }
}

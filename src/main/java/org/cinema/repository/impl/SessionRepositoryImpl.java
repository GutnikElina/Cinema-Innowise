package org.cinema.repository.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.FilmSession;
import org.cinema.repository.BaseRepository;
import org.cinema.repository.SessionRepository;
import org.hibernate.query.Query;
import java.util.*;

@Slf4j
public class SessionRepositoryImpl extends BaseRepository implements SessionRepository {

    @Getter
    private static final SessionRepositoryImpl instance = new SessionRepositoryImpl();

    public SessionRepositoryImpl() {
        super(HibernateConfig.getSessionFactory());
    }

    @Override
    public void save(FilmSession filmSession) {
        executeTransaction(session -> session.save(filmSession));
        log.info("Film session [{}] successfully added.", filmSession);
    }

    @Override
    public Optional<FilmSession> getById(int id) {
        return Optional.ofNullable(executeWithResult(session -> session.get(FilmSession.class, id)));
    }

    @Override
    public Set<FilmSession> findAll() {
        return executeWithResult(session -> {
            log.debug("Retrieving all film sessions...");
            List<FilmSession> filmSessions = session.createQuery("FROM FilmSession", FilmSession.class).list();

            log.info("{} users successfully retrieved.", filmSessions.size());
            return new HashSet<>(filmSessions);
        });
    }

    @Override
    public void update(FilmSession filmSession) {
        executeTransaction(session -> {
            session.merge(filmSession);
            log.info("Film session with ID [{}] successfully updated.", filmSession.getId());
        });
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            FilmSession filmSession = session.get(FilmSession.class, id);
            if (filmSession == null) {
                throw new NoDataFoundException("Film session with ID " + id + " doesn't exist.");
            }
            session.delete(filmSession);
            log.info("Film session with ID {} successfully deleted.", id);
        });
    }

    @Override
    public boolean checkIfSessionExists(FilmSession filmSession) {
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
    }
}
package org.cinema.repository.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.model.FilmSession;
import org.cinema.repository.AbstractHibernateRepository;
import org.cinema.repository.SessionRepository;
import org.hibernate.query.Query;
import java.time.LocalDate;
import java.util.*;
import java.util.Optional;

@Slf4j
public class SessionRepositoryImpl extends AbstractHibernateRepository<FilmSession> implements SessionRepository {

    @Getter
    private static final SessionRepositoryImpl instance = new SessionRepositoryImpl();

    public SessionRepositoryImpl() {
        super(HibernateConfig.getSessionFactory(), FilmSession.class);
    }

    @Override
    public void save(FilmSession filmSession) {
        super.save(filmSession);
        log.info("Film session successfully added.");
    }

    @Override
    public void update(FilmSession filmSession) {
        super.update(filmSession);
        log.info("Film session with ID '{}' successfully updated.", filmSession.getId());
    }

    @Override
    public void delete(long id) {
        super.delete(id);
    }

    @Override
    public Optional<FilmSession> getById(long id) {
        return super.getById(id);
    }

    @Override
    public Set<FilmSession> findAll() {
        return executeWithResult(session -> {
            log.debug("Retrieving all film sessions...");
            List<FilmSession> filmSessions = session.createQuery(
                "FROM FilmSession fs ORDER BY fs.date ASC, fs.startTime ASC", 
                FilmSession.class
            ).list();

            log.info("{} film sessions successfully retrieved.", filmSessions.size());
            return new HashSet<>(filmSessions);
        });
    }

    @Override
    public boolean checkIfSessionExists(FilmSession filmSession) {
        return executeWithResult(session -> {
            String hql = "FROM FilmSession fs WHERE fs.movie.id = :movieId AND fs.date = :date " +
                    "AND ((fs.startTime BETWEEN :start AND :end) OR (fs.endTime BETWEEN :start AND :end))";

            Query<FilmSession> query = session.createQuery(hql, FilmSession.class);
            query.setParameter("movieId", filmSession.getMovie().getId());
            query.setParameter("date", filmSession.getDate());
            query.setParameter("start", filmSession.getStartTime());
            query.setParameter("end", filmSession.getEndTime());

            return !query.list().isEmpty();
        });
    }

    @Override
    public Set<FilmSession> findByDate(LocalDate date) {
        return executeWithResult(session -> {
            String hql = "FROM FilmSession fs WHERE fs.date = :date";
            Query<FilmSession> query = session.createQuery(hql, FilmSession.class);
            query.setParameter("date", date);

            List<FilmSession> filmSessions = query.list();
            log.info("{} film sessions found for date: {}", filmSessions.size(), date);
            return new HashSet<>(filmSessions);
        });
    }
}
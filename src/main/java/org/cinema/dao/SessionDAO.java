package org.cinema.dao;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Session;
import org.cinema.model.User;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SessionDAO extends BaseDao implements Repository<Session>{

    @Override
    public void add(Session filmSession) {
        executeTransaction(session -> session.save(filmSession));
        log.info("Сеанс кинотеатра успешно добавлен.");
    }

    @Override
    public Optional<Session> getById(int id) {
        return Optional.ofNullable(executeTransactionWithResult(session -> {
            Session filmSession = session.get(Session.class, id);
            if (filmSession == null) {
                log.warn("Сеанс кинотеатра с ID {} не найден.", id);
            } else {
                log.info("Сеанс кинотеатра с ID {} успешно найден.", id);
            }
            return filmSession;
        }));
    }

    @Override
    public List<Session> getAll() {
        return executeTransactionWithResult(session -> {
            Query<Session> query = session.createQuery("FROM Session", Session.class);
            List<Session> sessions = query.list();

            if (sessions == null || sessions.isEmpty()) {
                log.warn("Сеансы кинотеатра не найдены в базе данных.");
                return Collections.emptyList();
            }
            log.info("{} сеансов успешно извлечены.", sessions.size());
            return sessions;
        });
    }

    @Override
    public void update(Session filmSession) {
        executeTransaction(session -> {
            Session existingSession = session.get(Session.class, filmSession.getId());
            if (existingSession != null) {
                session.merge(filmSession);
                log.info("Сеанс с ID {} успешно обновлен.", filmSession.getId());
            } else {
                log.warn("Сеанс с таким ID не существует.");
                throw new IllegalArgumentException("Сеанс с ID " + filmSession.getId() + " не существует.");
            }
        });
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            Session filmSession = session.get(Session.class, id);
            if (filmSession != null) {
                session.delete(filmSession);
                log.info("Сеанс с ID {} успешно удален.", id);
            } else {
                log.warn("Сеанс с ID {} не существует.", id);
                throw new IllegalArgumentException("Сеанс с ID " + id + " не существует.");
            }
        });
    }
}

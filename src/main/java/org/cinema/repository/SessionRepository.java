package org.cinema.repository;

import org.cinema.model.FilmSession;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface SessionRepository{
    void save(FilmSession filmSession);
    Optional<FilmSession> getById(int filmSessionId);
    Set<FilmSession> findAll();
    void update(FilmSession filmSession);
    void delete(int filmSessionId);
    boolean checkIfSessionExists(FilmSession filmSession);
    Set<FilmSession> findByDate(LocalDate date);
}


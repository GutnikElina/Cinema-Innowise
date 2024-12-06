package org.cinema.service;

import org.cinema.model.FilmSession;
import java.util.List;

public interface SessionService {
    List<FilmSession> findAll();
}

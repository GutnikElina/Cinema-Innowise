package org.cinema.service.impl;

import lombok.Getter;
import org.cinema.model.FilmSession;
import org.cinema.model.Ticket;
import org.cinema.repository.SessionRepository;
import org.cinema.repository.TicketRepository;
import org.cinema.repository.UserRepository;
import org.cinema.service.SessionService;
import org.hibernate.Session;

import java.util.List;

public class SessionServiceImpl implements SessionService {

    @Getter
    private static final SessionServiceImpl instance = new SessionServiceImpl();

    private static final SessionRepository sessionRepository = SessionRepository.getInstance();

    @Override
    public List<FilmSession> findAll() {
        return sessionRepository.findAll();
    }
}

package org.cinema.repository.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.Ticket;
import org.cinema.repository.BaseRepository;
import org.cinema.repository.TicketRepository;
import org.hibernate.query.Query;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class TicketRepositoryImpl extends BaseRepository implements TicketRepository {

    @Getter
    private static final TicketRepositoryImpl instance = new TicketRepositoryImpl();

    public TicketRepositoryImpl() {
        super(HibernateConfig.getSessionFactory());
    }

    @Override
    public void save(Ticket ticket) {
        executeTransaction(session -> session.save(ticket));
        log.info("Ticket [{}] successfully added.", ticket);
    }

    @Override
    public Optional<Ticket> getById(int id) {
        return Optional.ofNullable(executeWithResult(session -> session.get(Ticket.class, id)));
    }

    @Override
    public Set<Ticket> findAll() {
        return executeWithResult(session -> {
            log.debug("Retrieving all tickets...");
            List<Ticket> tickets = session.createQuery("FROM Ticket", Ticket.class).list();
            log.info("{} tickets successfully retrieved.", tickets.size());
            return new HashSet<>(tickets);
        });
    }

    @Override
    public void update(Ticket ticket, LocalDateTime purchaseTime) {
        executeTransaction(session -> {
            ticket.setPurchaseTime(purchaseTime);
            session.merge(ticket);
            log.info("Ticket with ID [{}] successfully updated.", ticket.getId());
        });
    }

    @Override
    public void delete(int id) {
        executeTransaction(session -> {
            Ticket ticket = session.get(Ticket.class, id);
            if (ticket == null) {
                throw new NoDataFoundException("Ticket with ID " + id + " doesn't exist.");
            }
            session.delete(ticket);
            log.info("Ticket with ID {} successfully deleted.", id);
        });
    }

    @Override
    public List<Ticket> getTicketsBySession(int sessionId) {
        return executeWithResult(session -> {
            Query<Ticket> query = session.createQuery(
                    "FROM Ticket t WHERE t.filmSession.id = :sessionId", Ticket.class);
            query.setParameter("sessionId", sessionId);

            List<Ticket> tickets = query.list();
            log.info("Found {} tickets for session with ID {}", tickets.size(), sessionId);
            return tickets;
        });
    }

    @Override
    public boolean checkIfTicketExists(Ticket ticket) {
        return executeWithResult(session -> {
            Query<Ticket> query = session.createQuery(
                    "FROM Ticket t WHERE t.filmSession.id = :sessionId " +
                            "AND t.seatNumber = :seatNumber", Ticket.class);
            query.setParameter("sessionId", ticket.getFilmSession().getId());
            query.setParameter("seatNumber", ticket.getSeatNumber());

            boolean exists = !query.list().isEmpty();
            log.debug("Check for existing ticket with session '{}', seat '{}': {}.",
                    ticket.getFilmSession().getId(), ticket.getSeatNumber(),
                    exists ? "found" : "not found");
            return exists;
        });
    }
}
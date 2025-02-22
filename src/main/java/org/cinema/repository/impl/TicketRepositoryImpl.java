package org.cinema.repository.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.model.Ticket;
import org.cinema.repository.AbstractHibernateRepository;
import org.cinema.repository.TicketRepository;
import org.hibernate.query.Query;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class TicketRepositoryImpl extends AbstractHibernateRepository<Ticket> implements TicketRepository {

    @Getter
    private static final TicketRepositoryImpl instance = new TicketRepositoryImpl();

    public TicketRepositoryImpl() {
        super(HibernateConfig.getSessionFactory(), Ticket.class);
    }

    @Override
    public void save(Ticket ticket) {
        super.save(ticket);
        log.info("Ticket successfully added.");
    }

    @Override
    public void update(Ticket ticket, LocalDateTime purchaseTime) {
        ticket.setPurchaseTime(purchaseTime);
        super.update(ticket);
        log.info("Ticket with ID {} successfully updated.", ticket.getId());
    }

    @Override
    public void delete(long id) {
        super.delete(id);
    }

    @Override
    public Optional<Ticket> getById(long id) {
        return super.getById(id);
    }

    @Override
    public Set<Ticket> findAll() {
        return executeWithResult(session -> {
            log.debug("Retrieving all tickets...");
            List<Ticket> tickets = session.createQuery(
                "FROM Ticket t JOIN FETCH t.filmSession fs " +
                "ORDER BY fs.date ASC, fs.startTime ASC, t.seatNumber ASC", 
                Ticket.class
            ).list();
            return new HashSet<>(tickets);
        });
    }

    @Override
    public List<Ticket> getTicketsBySession(long sessionId) {
        return executeWithResult(session -> {
            Query<Ticket> query = session.createQuery(
                "FROM Ticket t WHERE t.filmSession.id = :sessionId " +
                "ORDER BY t.seatNumber ASC", 
                Ticket.class
            );
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

    @Override
    public List<Ticket> getTicketsByUserId(long userId) {
        return executeWithResult(session -> {
            Query<Ticket> query = session.createQuery(
                "FROM Ticket t JOIN FETCH t.filmSession fs " +
                "WHERE t.user.id = :userId " +
                "ORDER BY fs.date ASC, fs.startTime ASC, t.seatNumber ASC", 
                Ticket.class
            );
            query.setParameter("userId", userId);

            List<Ticket> tickets = query.list();
            log.info("Found {} tickets for user with ID {}", tickets.size(), userId);
            return tickets;
        });
    }
}

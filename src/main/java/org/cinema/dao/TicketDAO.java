package org.cinema.dao;

import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.model.Ticket;
import org.hibernate.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TicketDAO extends BaseDao implements Repository<Ticket> {

    public TicketDAO() {
        super(HibernateConfig.getSessionFactory());
    }

    @Override
    public void add(Ticket ticket) {
        try {
            if (checkIfTicketExists(ticket)) {
                String errorMessage = "Ticket already exists: " + ticket;
                log.warn("Error while adding ticket: {}", errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            executeTransaction(session -> session.save(ticket));
            log.info("Ticket [{}] successfully added.", ticket);
        } catch (Exception e) {
            log.error("Unexpected error while adding ticket: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while adding ticket.", e);
        }
    }

    @Override
    public Optional<Ticket> getById(int id) {
        try {
            return Optional.ofNullable(executeTransactionWithResult(session -> {
                Ticket ticket = session.get(Ticket.class, id);
                if (ticket == null) {
                    log.warn("Ticket with ID {} not found.", id);
                } else {
                    log.info("Ticket with ID {} successfully found.", id);
                }
                return ticket;
            }));
        } catch (Exception e) {
            log.error("Unexpected error while retrieving ticket by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Unexpected error while retrieving ticket by ID.", e);
        }
    }

    @Override
    public List<Ticket> getAll() {
        try {
            return executeTransactionWithResult(session -> {
                List<Ticket> tickets = session.createQuery("FROM Ticket", Ticket.class).list();
                if (tickets.isEmpty()) {
                    log.warn("No tickets found in the database.");
                    return Collections.emptyList();
                }
                log.info("{} tickets successfully retrieved.", tickets.size());
                return tickets;
            });
        } catch (Exception e) {
            log.error("Unexpected error while retrieving tickets: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while retrieving tickets.", e);
        }
    }

    @Override
    public void update(Ticket ticket) {
        try {
            executeTransaction(session -> {
                Ticket existingTicket = session.get(Ticket.class, ticket.getId());
                if (existingTicket == null) {
                    String errorMessage = "Ticket with ID " + ticket.getId() + " doesn't exist.";
                    log.warn("Error while updating ticket: {}", errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
                session.merge(ticket);
                log.info("Ticket with ID [{}] successfully updated.", ticket.getId());
            });
        } catch (Exception e) {
            log.error("Unexpected error while updating ticket with ID {}: {}", ticket.getId(), e.getMessage());
            throw new RuntimeException("Unexpected error while updating ticket.", e);
        }
    }

    @Override
    public void delete(int id) {
        try {
            executeTransaction(session -> {
                Ticket ticket = session.get(Ticket.class, id);
                if (ticket == null) {
                    String errorMessage = "Ticket with ID " + id + " doesn't exist.";
                    log.warn(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
                session.delete(ticket);
                log.info("Ticket with ID [{}] successfully deleted.", id);
            });
        } catch (Exception e) {
            log.error("Unexpected error while deleting ticket with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Unexpected error while deleting ticket.", e);
        }
    }

    private boolean checkIfTicketExists(Ticket ticket) {
        try {
            return executeTransactionWithResult(session -> {
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
        } catch (Exception e) {
            log.error("Unexpected error while checking for existing ticket: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while checking if ticket exists.", e);
        }
    }
}

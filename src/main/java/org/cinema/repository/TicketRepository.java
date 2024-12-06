package org.cinema.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.model.Ticket;
import org.hibernate.query.Query;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TicketRepository extends BaseRepository implements Repository<Ticket> {

    @Getter
    private static final TicketRepository instance = new TicketRepository();

    public TicketRepository() {
        super(HibernateConfig.getSessionFactory());
    }

    @Override
    public void save(Ticket ticket) {
        try {
            if (checkIfTicketExists(ticket)) {
                String errorMessage = "Ticket already exists with this session and seat. Try again.";
                log.warn("Error while adding ticket: {}", errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            executeTransaction(session -> session.save(ticket));
            log.info("Ticket [{}] successfully added.", ticket);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding ticket: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while adding ticket.", e);
        }
    }

    @Override
    public Optional<Ticket> getById(int id) {
        try {
            return Optional.ofNullable(executeWithResult(session -> {
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
    public List<Ticket> findAll() {
        try {
            return executeWithResult(session -> {
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

                if (!(existingTicket.getFilmSession().equals(ticket.getFilmSession()) &&
                        existingTicket.getSeatNumber().equals(ticket.getSeatNumber()) )) {
                    if (checkIfTicketExists(ticket)) {
                        String errorMessage = "Ticket already exists with this session and seat. Try again.";
                        log.warn("Error while updating ticket: {}", errorMessage);
                        throw new IllegalArgumentException(errorMessage);
                    }
                }
                ticket.setPurchaseTime(existingTicket.getPurchaseTime());
                session.merge(ticket);
                log.info("Ticket with ID [{}] successfully updated.", ticket.getId());
            });
        } catch (IllegalArgumentException e) {
            throw e;
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
                log.info("Ticket with ID {} successfully deleted.", id);
            });
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting ticket with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Unexpected error while deleting ticket.", e);
        }
    }

    public List<Ticket> getTicketsBySession(int sessionId) {
        try {
            return executeWithResult(session -> {
                Query<Ticket> query = session.createQuery(
                        "FROM Ticket t WHERE t.filmSession.id = :sessionId", Ticket.class);
                query.setParameter("sessionId", sessionId);

                List<Ticket> tickets = query.list();
                log.info("Found {} tickets for session with ID {}", tickets.size(), sessionId);
                return tickets;
            });
        } catch (Exception e) {
            log.error("Unexpected error while retrieving tickets for session ID {}: {}", sessionId, e.getMessage());
            throw new RuntimeException("Unexpected error while retrieving tickets for session.", e);
        }
    }

    private boolean checkIfTicketExists(Ticket ticket) {
        try {
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
        } catch (Exception e) {
            log.error("Unexpected error while checking for existing ticket: {}", e.getMessage());
            throw new RuntimeException("Unexpected error while checking if ticket exists.", e);
        }
    }
}

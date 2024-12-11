package org.cinema.repository;

import org.cinema.model.Ticket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TicketRepository {
    void save(Ticket ticket);
    Optional<Ticket> getById(int ticketId);
    Set<Ticket> findAll();
    void update(Ticket ticket, LocalDateTime purchaseTime);
    void delete(int ticketId);
    List<Ticket> getTicketsBySession(int sessionId);
    boolean checkIfTicketExists(Ticket ticket);
    List<Ticket> getTicketsByUserId(int userId);
}


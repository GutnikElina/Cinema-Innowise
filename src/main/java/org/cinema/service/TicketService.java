package org.cinema.service;

import org.cinema.model.FilmSession;
import org.cinema.model.Ticket;
import java.util.Optional;
import java.util.Set;

public interface TicketService {
    Optional<Ticket> getById(String ticketId);
    String delete(String id);
    Set<Ticket> findAll();
    String save(String userId, String sessionId, String seatNumber, String statusStr, String requestTypeStr);
    String update(String id, String userId, String sessionId, String seatNumber, String statusStr, String requestTypeStr);
    FilmSession getSessionDetailsWithTickets(String sessionId);
    String processTicketAction(String action, String ticketIdParam);
    String purchaseTicket(String userId, String sessionId, String seatNumber);
    Set<Ticket> findByUserId(String userId);
}

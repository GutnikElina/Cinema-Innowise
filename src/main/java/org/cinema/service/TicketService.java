package org.cinema.service;

import org.cinema.model.Ticket;
import java.util.List;
import java.util.Optional;

public interface TicketService {
    Optional<Ticket> getById(int ticketId);
    String delete(int id);
    List<Ticket> findAll();
    String save(int userId, int sessionId, String seatNumber, String statusStr, String requestTypeStr);
    String update(int id, int userId, int sessionId, String seatNumber, String statusStr, String requestTypeStr);
}

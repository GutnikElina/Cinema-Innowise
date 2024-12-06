package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.*;
import org.cinema.repository.SessionRepository;
import org.cinema.repository.TicketRepository;
import org.cinema.repository.UserRepository;
import org.cinema.service.TicketService;
import org.cinema.util.ValidationUtil;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TicketServiceImpl implements TicketService {

    @Getter
    private static final TicketServiceImpl instance = new TicketServiceImpl();

    private static final TicketRepository ticketRepository = TicketRepository.getInstance();
    private static final UserRepository userRepository = UserRepository.getInstance();
    private static final SessionRepository sessionRepository = SessionRepository.getInstance();

    @Override
    public String save(int userId, int sessionId, String seatNumber, String statusStr, String requestTypeStr) {
        try {
            Status status = Status.valueOf(statusStr.toUpperCase());
            RequestType requestType = RequestType.valueOf(requestTypeStr.toUpperCase());

            User user = userRepository.getById(userId).orElseThrow(() -> new IllegalArgumentException("User with this ID doesn't exist!"));
            FilmSession filmSession = sessionRepository.getById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session with this ID doesn't exist!"));

            ValidationUtil.validateSeatNumber(seatNumber, filmSession.getCapacity());

            Ticket ticket = new Ticket(0, user, filmSession, seatNumber, null, status, requestType);
            ticketRepository.save(ticket);
            return "Success! Ticket was successfully added to the database!";
        } catch (IllegalArgumentException e) {
            log.error("Occurred error while adding ticket: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Error adding ticket: {}", e.getMessage(), e);
            return "Error! Failed to add ticket. Please check the entered data.";
        }
    }

    @Override
    public String update(int id, int userId, int sessionId, String seatNumber, String statusStr, String requestTypeStr) {
        try {
            Status status = Status.valueOf(statusStr.toUpperCase());
            RequestType requestType = RequestType.valueOf(requestTypeStr.toUpperCase());

            User user = userRepository.getById(userId).orElseThrow(() -> new IllegalArgumentException("User with this ID doesn't exist!"));
            FilmSession filmSession = sessionRepository.getById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session with this ID doesn't exist!"));

            ValidationUtil.validateSeatNumber(seatNumber, filmSession.getCapacity());

            Ticket ticket = new Ticket(id, user, filmSession, seatNumber, null, status, requestType);
            ticketRepository.update(ticket);
            return "Success! Ticket was successfully updated in the database!";
        } catch (IllegalArgumentException e) {
            log.error("Occurred error while updating ticket: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Error updating ticket: {}", e.getMessage(), e);
            return "Error! Failed to update ticket. Please check the entered data.";
        }
    }

    @Override
    public String delete(int id) {
        try {
            ticketRepository.delete(id);
            return "Success! Ticket was successfully deleted from the database!";
        } catch (NumberFormatException e) {
            log.error("Invalid ticket ID format during delete: {}", e.getMessage(), e);
            return "Error! Invalid ticket ID format.";
        }
    }

    @Override
    public Optional<Ticket> getById(int ticketId) {
        return ticketRepository.getById(ticketId);
    }

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }
}

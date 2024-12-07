package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.*;
import org.cinema.repository.SessionRepository;
import org.cinema.repository.TicketRepository;
import org.cinema.repository.UserRepository;
import org.cinema.service.TicketService;
import org.cinema.util.ValidationUtil;
import java.time.LocalDateTime;
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

    @Override
    public void purchaseTicket(int userId, int sessionId, String seatNumber) {
        try {
            User user = userRepository.getById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
            FilmSession session = sessionRepository.getById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found with ID: " + sessionId));

            ValidationUtil.validateSeatNumber(seatNumber, session.getCapacity());
            log.info("Seat number {} validated successfully for session {}.", seatNumber, sessionId);

            Ticket ticket = new Ticket();
            ticket.setUser(user);
            ticket.setFilmSession(session);
            ticket.setSeatNumber(seatNumber);
            ticket.setStatus(Status.PENDING);
            ticket.setRequestType(RequestType.PURCHASE);
            ticket.setPurchaseTime(LocalDateTime.now());

            ticketRepository.save(ticket);
            log.info("Ticket successfully created for session {} and seat {}.", sessionId, seatNumber);
        } catch (Exception e) {
            log.error("Error during ticket purchase: {}", e.getMessage(), e);
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public FilmSession getSessionDetailsWithTickets(int sessionId) {
        try {
            FilmSession session = sessionRepository.getById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found with ID: " + sessionId));

            List<Ticket> tickets = ticketRepository.getTicketsBySession(sessionId);
            List<Integer> takenSeats = tickets.stream()
                    .map(ticket -> Integer.parseInt(ticket.getSeatNumber()))
                    .toList();

            session.setTakenSeats(takenSeats);
            return session;
        } catch (Exception e) {
            log.error("Error loading session details: {}", e.getMessage(), e);
            throw new IllegalStateException("Unable to load session details.");
        }
    }

    @Override
    public String processTicketAction(String action, String ticketIdParam) {
        try {
            ValidationUtil.validateParameters(action, ticketIdParam);
            int ticketId = Integer.parseInt(ticketIdParam);
            Ticket ticket = getById(ticketId).orElseThrow(() ->
                    new IllegalArgumentException("Ticket with this ID doesn't exist!"));

            return switch (action) {
                case "confirm" -> confirmTicket(ticket);
                case "return" -> returnTicket(ticket);
                case "cancel" -> cancelTicket(ticket);
                default -> {
                    log.warn("Unknown action: {}", action);
                    yield "Error! Unknown action.";
                }
            };
        } catch (IllegalArgumentException e) {
            log.error("Validation error in processTicketAction: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in processTicketAction: {}", e.getMessage(), e);
            throw new IllegalStateException("An unknown error occurred.");
        }
    }

    private String confirmTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING && ticket.getRequestType() == RequestType.PURCHASE) {
            ticket.setStatus(Status.CONFIRMED);
            ticketRepository.update(ticket);
            return "Success! Ticket Confirmed!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String returnTicket(Ticket ticket) {
        if (ticket.getRequestType() == RequestType.RETURN) {
            ticket.setStatus(Status.RETURNED);
            ticketRepository.update(ticket);
            return "Success! Ticket Returned!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String cancelTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING) {
            ticket.setStatus(Status.CANCELLED);
            ticketRepository.update(ticket);
            return "Success! Ticket Cancelled!";
        }
        return "Error! Invalid action for this ticket.";
    }

}

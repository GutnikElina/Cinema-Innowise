package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.*;
import org.cinema.repository.impl.SessionRepositoryImpl;
import org.cinema.repository.impl.TicketRepositoryImpl;
import org.cinema.repository.impl.UserRepositoryImpl;
import org.cinema.service.TicketService;
import org.cinema.util.ValidationUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class TicketServiceImpl implements TicketService {

    @Getter
    private static final TicketServiceImpl instance = new TicketServiceImpl();

    private final TicketRepositoryImpl ticketRepository = TicketRepositoryImpl.getInstance();
    private final UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();
    private final SessionRepositoryImpl sessionRepository = SessionRepositoryImpl.getInstance();

    @Override
    public String save(String userId, String sessionId, String seatNumber, String statusStr, String requestTypeStr) {

        Status status = Status.valueOf(statusStr.toUpperCase());
        RequestType requestType = RequestType.valueOf(requestTypeStr.toUpperCase());

        User user = userRepository.getById(ValidationUtil.parseId(userId)).orElseThrow(() ->
                new NoDataFoundException("User with this ID doesn't exist!"));
        FilmSession filmSession = sessionRepository.getById(ValidationUtil.parseId(sessionId)).orElseThrow(() ->
                new NoDataFoundException("Session with this ID doesn't exist!"));

        ValidationUtil.validateSeatNumber(seatNumber, filmSession.getCapacity());

        Ticket ticket = new Ticket(0, user, filmSession, seatNumber, null, status, requestType);

        if (ticketRepository.checkIfTicketExists(ticket)) {
            throw new EntityAlreadyExistException("Ticket already exists with this session and seat. Try again.");
        }

        ticketRepository.save(ticket);

        if (!ticketRepository.checkIfTicketExists(ticket)) {
            throw new NoDataFoundException("Ticket not found in database after saving. Try again.");
        }
        return "Success! Ticket was successfully added to the database!";
    }

    @Override
    public String update(String id, String userId, String sessionId, String seatNumber, String statusStr, String requestTypeStr) {
        Status status = Status.valueOf(statusStr.toUpperCase());
        RequestType requestType = RequestType.valueOf(requestTypeStr.toUpperCase());

        User user = userRepository.getById(ValidationUtil.parseId(userId)).orElseThrow(() ->
                new NoDataFoundException("User with this ID doesn't exist!"));
        FilmSession filmSession = sessionRepository.getById(ValidationUtil.parseId(sessionId)).orElseThrow(() ->
                new NoDataFoundException("Session with this ID doesn't exist!"));

        ValidationUtil.validateSeatNumber(seatNumber, filmSession.getCapacity());

        int ticketId = ValidationUtil.parseId(id);
        Ticket ticket = new Ticket(ticketId, user, filmSession, seatNumber, null, status, requestType);

        Ticket existingTicket = ticketRepository.getById(ticketId).orElseThrow(() ->
                new NoDataFoundException("Ticket with this ID doesn't exist!"));

        ticketRepository.update(ticket, existingTicket.getPurchaseTime());

        if (!ticketRepository.checkIfTicketExists(ticket)) {
            throw new NoDataFoundException("Ticket not found in database after updating. Try again.");
        }

        return "Success! Ticket was successfully updated in the database!";
    }

    @Override
    public String delete(String ticketIdStr) {
        ticketRepository.delete(ValidationUtil.parseId(ticketIdStr));
        return "Success! Ticket was successfully deleted!";
    }

    @Override
    public Optional<Ticket> getById(String ticketIdStr) {
        return ticketRepository.getById(ValidationUtil.parseId(ticketIdStr));
    }

    @Override
    public Set<Ticket> findAll() {
        Set<Ticket> tickets = ticketRepository.findAll();

        if (tickets.isEmpty()) {
            throw new NoDataFoundException("No tickets found in the database.");
        }

        log.info("{} tickets retrieved successfully.", tickets.size());
        return tickets;
    }

    @Override
    public String purchaseTicket(String userId, String sessionId, String seatNumber) {

        User user = userRepository.getById(ValidationUtil.parseId(userId))
                .orElseThrow(() -> new NoDataFoundException("User not found with ID: " + userId));
        FilmSession session = sessionRepository.getById(ValidationUtil.parseId(sessionId))
                .orElseThrow(() -> new NoDataFoundException("Session not found with ID: " + sessionId));

        ValidationUtil.validateSeatNumber(seatNumber, session.getCapacity());
        log.debug("Seat number {} validated successfully for session {}.", seatNumber, sessionId);

        Ticket ticket = new Ticket(0, user, session, seatNumber, null, Status.PENDING, RequestType.PURCHASE);
        if (ticketRepository.checkIfTicketExists(ticket)) {
            throw new EntityAlreadyExistException("Ticket already exists with this session and seat. Try again.");
        }

        ticketRepository.save(ticket);
        if (!ticketRepository.checkIfTicketExists(ticket)) {
            throw new NoDataFoundException("Ticket not found in database after purchasing. Try again.");
        }
        log.info("Ticket successfully created for session {} and seat {}.", sessionId, seatNumber);
        return "Success! Ticket purchased, awaiting confirmation.";
    }

    @Override
    public FilmSession getSessionDetailsWithTickets(String sessionIdStr) {
        int sessionId = ValidationUtil.parseId(sessionIdStr);
        FilmSession session = sessionRepository.getById(sessionId)
                .orElseThrow(() -> new NoDataFoundException("Session not found with ID: " + sessionId));

        List<Ticket> tickets = ticketRepository.getTicketsBySession(sessionId);
        List<Integer> takenSeats = tickets.stream()
                .map(ticket -> Integer.parseInt(ticket.getSeatNumber()))
                .toList();

        session.setTakenSeats(takenSeats);
        return session;
    }

    @Override
    public Set<Ticket> findByUserId(String userId) {
        int parsedUserId = ValidationUtil.parseId(userId);
        List<Ticket> ticketsList = ticketRepository.getTicketsByUserId(parsedUserId);

        if (ticketsList.isEmpty()) {
            throw new NoDataFoundException("Your tickets are absent!");
        }

        Set<Ticket> tickets = new HashSet<>(ticketsList);
        log.info("{} tickets found for user with ID: {}", tickets.size(), userId);
        return tickets;
    }


    @Override
    public String processTicketAction(String action, String ticketIdParam) {

        ValidationUtil.validateParameters(action, ticketIdParam);
        Ticket ticket = getById(ticketIdParam).orElseThrow(() ->
                new NoDataFoundException("Ticket with this ID doesn't exist!"));

        return switch (action) {
            case "confirm" -> confirmTicket(ticket);
            case "return" -> returnTicket(ticket);
            case "cancel" -> cancelTicket(ticket);
            case "returnMyTicket" -> returnMyTicket(ticket);
            default -> {
                log.warn("Unknown action: {}", action);
                yield "Error! Unknown action.";
            }
        };
    }

    private String returnMyTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING) {
            ticket.setRequestType(RequestType.RETURN);
            ticketRepository.update(ticket, ticket.getPurchaseTime());
            return "Success! Ticket Returned!";
        }
        return "Error! Ticket cannot be returned.";
    }

    private String confirmTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING && ticket.getRequestType() == RequestType.PURCHASE) {
            ticket.setStatus(Status.CONFIRMED);
            ticketRepository.update(ticket, ticket.getPurchaseTime());
            return "Success! Ticket Confirmed!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String returnTicket(Ticket ticket) {
        if (ticket.getRequestType() == RequestType.RETURN) {
            ticket.setStatus(Status.RETURNED);
            ticketRepository.update(ticket, ticket.getPurchaseTime());
            return "Success! Ticket Returned!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String cancelTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING) {
            ticket.setStatus(Status.CANCELLED);
            ticketRepository.update(ticket, ticket.getPurchaseTime());
            return "Success! Ticket Cancelled!";
        }
        return "Error! Invalid action for this ticket.";
    }
}

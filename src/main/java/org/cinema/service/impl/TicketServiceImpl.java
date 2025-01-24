package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.mapper.filmSessionMapper.FilmSessionResponseMapper;
import org.cinema.mapper.ticketMapper.TicketCreateMapper;
import org.cinema.mapper.ticketMapper.TicketResponseMapper;
import org.cinema.mapper.ticketMapper.TicketUpdateMapper;
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
import java.util.stream.Collectors;

@Slf4j
public class TicketServiceImpl implements TicketService {

    @Getter
    private static final TicketServiceImpl instance = new TicketServiceImpl();

    private final TicketRepositoryImpl ticketRepository = TicketRepositoryImpl.getInstance();
    private final UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();
    private final SessionRepositoryImpl sessionRepository = SessionRepositoryImpl.getInstance();

    @Override
    public String save(TicketCreateDTO createDTO) {
        Status status = Status.valueOf(createDTO.getStatus().toUpperCase());
        RequestType requestType = RequestType.valueOf(createDTO.getRequestType().toUpperCase());

        User user = userRepository.getById(createDTO.getUserId()).orElseThrow(() ->
                new NoDataFoundException("User with this ID doesn't exist!"));
        FilmSession filmSession = sessionRepository.getById(createDTO.getSessionId()).orElseThrow(() ->
                new NoDataFoundException("Session with this ID doesn't exist!"));

        ValidationUtil.validateSeatNumber(createDTO.getSeatNumber(), filmSession.getCapacity());

        Ticket ticket = TicketCreateMapper.INSTANCE.toEntity(createDTO);
        ticket.setUser(user);
        ticket.setFilmSession(filmSession);
        ticket.setStatus(status);
        ticket.setRequestType(requestType);

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
    public String update(TicketUpdateDTO updateDTO) {
        Status status = Status.valueOf(updateDTO.getStatus().toUpperCase());
        RequestType requestType = RequestType.valueOf(updateDTO.getRequestType().toUpperCase());

        User user = userRepository.getById(updateDTO.getUserId()).orElseThrow(() ->
                new NoDataFoundException("User with this ID doesn't exist!"));
        FilmSession filmSession = sessionRepository.getById(updateDTO.getSessionId()).orElseThrow(() ->
                new NoDataFoundException("Session with this ID doesn't exist!"));

        ValidationUtil.validateSeatNumber(updateDTO.getSeatNumber(), filmSession.getCapacity());

        Ticket ticket = TicketUpdateMapper.INSTANCE.toEntity(updateDTO);
        ticket.setUser(user);
        ticket.setFilmSession(filmSession);
        ticket.setStatus(status);
        ticket.setRequestType(requestType);

        Ticket existingTicket = ticketRepository.getById(ticket.getId()).orElseThrow(() ->
                new NoDataFoundException("Ticket with this ID doesn't exist!"));

        ticketRepository.update(ticket, existingTicket.getPurchaseTime());

        if (!ticketRepository.checkIfTicketExists(ticket)) {
            throw new NoDataFoundException("Ticket not found in database after updating. Try again.");
        }

        return "Success! Ticket was successfully updated in the database!";
    }

    @Override
    public String delete(String ticketIdStr) {
        ticketRepository.delete(ValidationUtil.parseLong(ticketIdStr));
        return "Success! Ticket was successfully deleted!";
    }

    @Override
    public Optional<TicketResponseDTO> getById(String ticketIdStr) {
        return ticketRepository.getById(ValidationUtil.parseLong(ticketIdStr))
                .map(TicketResponseMapper.INSTANCE::toDTO);
    }

    @Override
    public Set<TicketResponseDTO> findAll() {
        Set<Ticket> tickets = ticketRepository.findAll();

        if (tickets.isEmpty()) {
            throw new NoDataFoundException("No tickets found in the database.");
        }

        log.info("{} tickets retrieved successfully.", tickets.size());
        return tickets.stream()
                .map(TicketResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public String purchaseTicket(TicketCreateDTO ticketCreateDTO) {
        User user = userRepository.getById(ticketCreateDTO.getUserId())
                .orElseThrow(() -> new NoDataFoundException("User not found with ID: " + ticketCreateDTO.getUserId()));
        FilmSession session = sessionRepository.getById(ticketCreateDTO.getSessionId())
                .orElseThrow(() -> new NoDataFoundException("Session not found with ID: " + ticketCreateDTO.getSessionId()));

        ValidationUtil.validateSeatNumber(ticketCreateDTO.getSeatNumber(), session.getCapacity());
        log.debug("Seat number {} validated successfully for session {}.", ticketCreateDTO.getSeatNumber(), session.getId());

        Ticket ticket = TicketCreateMapper.INSTANCE.toEntity(ticketCreateDTO);
        ticket.setUser(user);
        ticket.setFilmSession(session);
        ticket.setStatus(Status.PENDING);
        ticket.setRequestType(RequestType.PURCHASE);

        if (ticketRepository.checkIfTicketExists(ticket)) {
            throw new EntityAlreadyExistException("Ticket already exists with this session and seat. Try again.");
        }

        ticketRepository.save(ticket);
        if (!ticketRepository.checkIfTicketExists(ticket)) {
            throw new NoDataFoundException("Ticket not found in database after purchasing. Try again.");
        }
        log.info("Ticket successfully created for session {} and seat {}.", session.getId(), ticket.getSeatNumber());
        return "Success! Ticket purchased, awaiting confirmation.";
    }

    @Override
    public FilmSessionResponseDTO getSessionDetailsWithTickets(String sessionIdStr) {
        int sessionId = ValidationUtil.parseId(sessionIdStr);
        FilmSession session = sessionRepository.getById(sessionId)
                .orElseThrow(() -> new NoDataFoundException("Session not found with ID: " + sessionId));

        List<Ticket> tickets = ticketRepository.getTicketsBySession(sessionId);
        List<Integer> takenSeats = tickets.stream()
                .map(ticket -> Integer.parseInt(ticket.getSeatNumber()))
                .toList();

        FilmSessionResponseDTO sessionResponseDTO= FilmSessionResponseMapper.INSTANCE.toDTO(session);
        sessionResponseDTO.setTakenSeats(takenSeats);
        return sessionResponseDTO;
    }

    @Override
    public Set<TicketResponseDTO> findByUserId(String userId) {
        int parsedUserId = ValidationUtil.parseId(userId);
        List<Ticket> ticketsList = ticketRepository.getTicketsByUserId(parsedUserId);

        if (ticketsList.isEmpty()) {
            throw new NoDataFoundException("Your tickets are absent!");
        }

        Set<Ticket> tickets = new HashSet<>(ticketsList);
        log.info("{} tickets found for user with ID: {}", tickets.size(), userId);
        return tickets.stream()
                .map(TicketResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toSet());
    }


    @Override
    public String processTicketAction(String action, Long ticketId) {
        Ticket ticket = ticketRepository.getById(ticketId).orElseThrow(() ->
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

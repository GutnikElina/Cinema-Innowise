package org.cinema.service.impl;

import jakarta.transaction.Transactional;
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
import org.cinema.model.*;
import org.cinema.repository.SessionRepository;
import org.cinema.repository.TicketRepository;
import org.cinema.repository.UserRepository;
import org.cinema.service.TicketService;
import org.cinema.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    private TicketServiceImpl(TicketRepository ticketRepository,
                              UserRepository userRepository,
                              SessionRepository sessionRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    @Transactional
    public String save(TicketCreateDTO createDTO) {
        Status status = Status.valueOf(createDTO.getStatus().toUpperCase());
        RequestType requestType = RequestType.valueOf(createDTO.getRequestType().toUpperCase());

        User user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new NoDataFoundException("User with this ID doesn't exist!"));
        FilmSession filmSession = sessionRepository.findById(createDTO.getSessionId())
                .orElseThrow(() -> new NoDataFoundException("Session with this ID doesn't exist!"));

        if (ticketRepository.existsBySessionAndSeat(filmSession.getId(), Integer.parseInt(createDTO.getSeatNumber()))) {
            throw new EntityAlreadyExistException("Ticket already exists with this session and seat. Try again.");
        }

        ValidationUtil.validateSeatNumber(createDTO.getSeatNumber(), filmSession.getCapacity());

        Ticket ticket = TicketCreateMapper.INSTANCE.toEntity(createDTO);
        ticket.setUser(user);
        ticket.setFilmSession(filmSession);
        ticket.setStatus(status);
        ticket.setRequestType(requestType);

        if (ticket.getPurchaseTime() == null) {
            ticket.setPurchaseTime(LocalDateTime.now());
        }
        ticketRepository.save(ticket);

        return "Success! Ticket was successfully added to the database!";
    }

    @Override
    @Transactional
    public String update(TicketUpdateDTO updateDTO) {
        Ticket existingTicket = ticketRepository.findById(updateDTO.getId())
                .orElseThrow(() -> new NoDataFoundException("Ticket with this ID doesn't exist!"));

        Status status = Status.valueOf(updateDTO.getStatus().toUpperCase());
        RequestType requestType = RequestType.valueOf(updateDTO.getRequestType().toUpperCase());

        User user = userRepository.findById(updateDTO.getUserId())
                .orElseThrow(() -> new NoDataFoundException("User with this ID doesn't exist!"));
        FilmSession filmSession = sessionRepository.findById(updateDTO.getSessionId())
                .orElseThrow(() -> new NoDataFoundException("Session with this ID doesn't exist!"));

        ValidationUtil.validateSeatNumber(updateDTO.getSeatNumber(), filmSession.getCapacity());

        existingTicket.setStatus(status);
        existingTicket.setRequestType(requestType);
        existingTicket.setUser(user);
        existingTicket.setFilmSession(filmSession);
        existingTicket.setSeatNumber(updateDTO.getSeatNumber());

        ticketRepository.save(existingTicket);

        return "Success! Ticket was successfully updated in the database!";
    }

    @Override
    @Transactional
    public String delete(String ticketIdStr) {
        Long ticketId = ValidationUtil.parseLong(ticketIdStr);
        ticketRepository.deleteById(ticketId);
        return "Success! Ticket was successfully deleted!";
    }

    @Override
    public Optional<TicketResponseDTO> getById(String ticketIdStr) {
        Long ticketId = ValidationUtil.parseLong(ticketIdStr);
        return ticketRepository.findById(ticketId)
                .map(TicketResponseMapper.INSTANCE::toDTO);
    }

    @Override
    public Set<TicketResponseDTO> findAll() {
        List<Ticket> tickets = ticketRepository.findAll();
        if (tickets.isEmpty()) {
            throw new NoDataFoundException("No tickets found in the database.");
        }
        log.info("{} tickets retrieved successfully.", tickets.size());
        return tickets.stream()
                .map(TicketResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public String purchaseTicket(TicketCreateDTO ticketCreateDTO) {
        User user = userRepository.findById(ticketCreateDTO.getUserId())
                .orElseThrow(() -> new NoDataFoundException("User not found with ID: " + ticketCreateDTO.getUserId()));
        FilmSession session = sessionRepository.findById(ticketCreateDTO.getSessionId())
                .orElseThrow(() -> new NoDataFoundException("Session not found with ID: " + ticketCreateDTO.getSessionId()));

        ValidationUtil.validateSeatNumber(ticketCreateDTO.getSeatNumber(), session.getCapacity());
        log.debug("Seat number {} validated successfully for session {}.", ticketCreateDTO.getSeatNumber(), session.getId());

        Ticket ticket = TicketCreateMapper.INSTANCE.toEntity(ticketCreateDTO);
        ticket.setUser(user);
        ticket.setFilmSession(session);
        ticket.setStatus(Status.PENDING);
        ticket.setRequestType(RequestType.PURCHASE);

        if (ticketRepository.existsBySessionAndSeat(session.getId(), Integer.parseInt(ticketCreateDTO.getSeatNumber()))) {
            throw new EntityAlreadyExistException("Ticket already exists with this session and seat. Try again.");
        }

        ticketRepository.save(ticket);
        log.info("Ticket successfully created for session {} and seat {}.", session.getId(), ticket.getSeatNumber());
        return "Success! Ticket purchased, awaiting confirmation.";
    }

    @Override
    public FilmSessionResponseDTO getSessionDetailsWithTickets(String sessionIdStr) {
        long sessionId = ValidationUtil.parseLong(sessionIdStr);
        FilmSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoDataFoundException("Session not found with ID: " + sessionId));

        List<Ticket> tickets = ticketRepository.findTicketsBySession(sessionId);
        List<Integer> takenSeats = tickets.stream()
                .map(ticket -> Integer.parseInt(ticket.getSeatNumber()))
                .collect(Collectors.toList());

        FilmSessionResponseDTO sessionResponseDTO = FilmSessionResponseMapper.INSTANCE.toDTO(session);
        sessionResponseDTO.setTakenSeats(takenSeats);
        return sessionResponseDTO;
    }

    @Override
    public Set<TicketResponseDTO> findByUserId(String userId) {
        long parsedUserId = ValidationUtil.parseLong(userId);
        List<Ticket> ticketsList = ticketRepository.findTicketsByUserId(parsedUserId);

        if (ticketsList.isEmpty()) {
            throw new NoDataFoundException("Your tickets are absent!");
        }

        Set<Ticket> tickets = Set.copyOf(ticketsList);
        log.info("{} tickets found for user with ID: {}", tickets.size(), userId);
        return tickets.stream()
                .map(TicketResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public String processTicketAction(String action, Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoDataFoundException("Ticket with this ID doesn't exist!"));

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
            ticketRepository.save(ticket);
            return "Success! Ticket Returned!";
        }
        return "Error! Ticket cannot be returned.";
    }

    private String confirmTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING && ticket.getRequestType() == RequestType.PURCHASE) {
            ticket.setStatus(Status.CONFIRMED);
            ticketRepository.save(ticket);
            return "Success! Ticket Confirmed!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String returnTicket(Ticket ticket) {
        if (ticket.getRequestType() == RequestType.RETURN) {
            ticket.setStatus(Status.RETURNED);
            ticketRepository.save(ticket);
            return "Success! Ticket Returned!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String cancelTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING) {
            ticket.setStatus(Status.CANCELLED);
            ticketRepository.save(ticket);
            return "Success! Ticket Cancelled!";
        }
        return "Error! Invalid action for this ticket.";
    }
}

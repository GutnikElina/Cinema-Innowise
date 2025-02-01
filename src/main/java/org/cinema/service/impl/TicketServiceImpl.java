package org.cinema.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public String save(TicketCreateDTO createDTO) {
        Status status = Status.valueOf(createDTO.getStatus().toUpperCase());
        RequestType requestType = RequestType.valueOf(createDTO.getRequestType().toUpperCase());
        User user = checkUserById(createDTO.getUserId());
        FilmSession filmSession = checkSessionById(createDTO.getSessionId());

        ValidationUtil.validateSeatNumber(createDTO.getSeatNumber(), filmSession.getCapacity());
        checkTicketExistence(filmSession.getId(), Integer.parseInt(createDTO.getSeatNumber()));

        Ticket ticket = createTicketFromDTO(createDTO, user, filmSession, status, requestType);
        ticketRepository.save(ticket);
        log.info("Ticket successfully added for movie '{}'.", filmSession.getMovie().getTitle());
        return "Success! Ticket was successfully added to the database!";
    }

    @Override
    @Transactional
    public String update(TicketUpdateDTO updateDTO) {
        Ticket existingTicket = checkTicketById(updateDTO.getId());

        Status status = Status.valueOf(updateDTO.getStatus().toUpperCase());
        RequestType requestType = RequestType.valueOf(updateDTO.getRequestType().toUpperCase());
        User user = checkUserById(updateDTO.getUserId());
        FilmSession filmSession = checkSessionById(updateDTO.getSessionId());

        ValidationUtil.validateSeatNumber(updateDTO.getSeatNumber(), filmSession.getCapacity());

        existingTicket.setStatus(status);
        existingTicket.setRequestType(requestType);
        existingTicket.setUser(user);
        existingTicket.setFilmSession(filmSession);
        existingTicket.setSeatNumber(updateDTO.getSeatNumber());

        ticketRepository.save(existingTicket);
        log.info("Ticket successfully updated with id '{}'.", existingTicket.getId());
        return "Success! Ticket was successfully updated in the database!";
    }

    @Override
    @Transactional
    public String delete(String ticketIdStr) {
        Long ticketId = ValidationUtil.parseLong(ticketIdStr);
        ticketRepository.deleteById(ticketId);
        log.info("Ticket successfully deleted with id '{}'.", ticketId);
        return "Success! Ticket was successfully deleted!";
    }

    @Override
    public TicketResponseDTO getById(String ticketIdStr) {
        Long ticketId = ValidationUtil.parseLong(ticketIdStr);
        return ticketRepository.findById(ticketId)
                .map(TicketResponseMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NoDataFoundException("Error! Ticket with ID " +
                        ticketId + " doesn't exist!"));
    }

    @Override
    public List<TicketResponseDTO> findAll() {
        List<Ticket> tickets = ticketRepository.findAll();
        if (tickets.isEmpty()) {
            throw new NoDataFoundException("Error! No tickets found in the database.");
        }
        log.info("{} tickets retrieved successfully.", tickets.size());
        return tickets.stream()
                .map(TicketResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String purchaseTicket(TicketCreateDTO ticketCreateDTO) {
        User user = checkUserById(ticketCreateDTO.getUserId());
        FilmSession session = checkSessionById(ticketCreateDTO.getSessionId());

        ValidationUtil.validateSeatNumber(ticketCreateDTO.getSeatNumber(), session.getCapacity());
        checkTicketExistence(session.getId(), Integer.parseInt(ticketCreateDTO.getSeatNumber()));

        Ticket ticket = createTicketFromDTO(ticketCreateDTO, user, session, Status.PENDING, RequestType.PURCHASE);
        ticketRepository.save(ticket);
        log.info("Ticket successfully purchased for session {} and seat {}.", session.getId(), ticket.getSeatNumber());
        return "Success! Ticket purchased, awaiting confirmation.";
    }

    @Override
    public FilmSessionResponseDTO getSessionDetailsWithTickets(String sessionIdStr) {
        Long sessionId = ValidationUtil.parseLong(sessionIdStr);
        FilmSession session = checkSessionById(sessionId);

        List<Ticket> tickets = ticketRepository.findByFilmSessionId(sessionId);
        List<Integer> takenSeats = tickets.stream()
                .map(ticket -> Integer.parseInt(ticket.getSeatNumber()))
                .collect(Collectors.toList());

        FilmSessionResponseDTO sessionResponseDTO = FilmSessionResponseMapper.INSTANCE.toDTO(session);
        sessionResponseDTO.setTakenSeats(takenSeats);
        return sessionResponseDTO;
    }

    @Override
    public List<TicketResponseDTO> findByUserId(String id) {
        Long userId = ValidationUtil.parseLong(id);
        List<Ticket> tickets = ticketRepository.findTicketsByUserId(userId);
        if (tickets.isEmpty()) {
            throw new NoDataFoundException("\"Error! Your tickets are absent!");
        }

        log.info("{} tickets found for user with ID: {}", tickets.size(), userId);
        return tickets.stream()
                .map(TicketResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String processTicketAction(String action, Long ticketId) {
        Ticket ticket = checkTicketById(ticketId);

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

    private String confirmTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING && ticket.getRequestType() == RequestType.PURCHASE) {
            ticket.setStatus(Status.CONFIRMED);
            ticketRepository.save(ticket);
            return "Success! Ticket Confirmed!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String returnMyTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING) {
            ticket.setRequestType(RequestType.RETURN);
            ticketRepository.save(ticket);
            return "Success! Ticket Returned!";
        }
        return "Error! Ticket cannot be returned.";
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

    private User checkUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoDataFoundException("Error! User with this ID doesn't exist!"));
    }

    private FilmSession checkSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoDataFoundException("Error! Session with this ID doesn't exist!"));
    }

    private Ticket checkTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoDataFoundException("Error! Ticket with this ID doesn't exist!"));
    }

    private void checkTicketExistence(Long sessionId, int seatNumber) {
        if (ticketRepository.existsByFilmSessionIdAndSeatNumber(sessionId, String.valueOf(seatNumber))) {
            throw new EntityAlreadyExistException("Error! Ticket already exists with this session and seat. Try again.");
        }
    }

    private Ticket createTicketFromDTO(TicketCreateDTO createDTO, User user, FilmSession filmSession,
                                       Status status, RequestType requestType) {
        Ticket ticket = TicketCreateMapper.INSTANCE.toEntity(createDTO);
        ticket.setUser(user);
        ticket.setFilmSession(filmSession);
        ticket.setStatus(status);
        ticket.setRequestType(requestType);

        if (ticket.getPurchaseTime() == null) {
            ticket.setPurchaseTime(LocalDateTime.now());
        }
        return ticket;
    }
}

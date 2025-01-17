package org.cinema.service;

import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import org.cinema.model.FilmSession;
import org.cinema.model.Ticket;
import java.util.Optional;
import java.util.Set;

public interface TicketService {
    String save(TicketCreateDTO ticketCreateDTO);
    String update(TicketUpdateDTO ticketUpdateDTO);
    String delete(String id);
    Optional<TicketResponseDTO> getById(String ticketId);
    Set<TicketResponseDTO> findAll();
    FilmSessionResponseDTO getSessionDetailsWithTickets(String sessionId);
    String purchaseTicket(TicketCreateDTO ticketCreateDTO);
    Set<TicketResponseDTO> findByUserId(String userId);
    String processTicketAction(String action, Long ticketId);
}

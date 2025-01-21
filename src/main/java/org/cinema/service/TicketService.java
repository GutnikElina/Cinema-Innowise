package org.cinema.service;

import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing tickets.
 */
public interface TicketService {
    /**
     * Creates a new ticket.
     *
     * @param ticketCreateDTO the DTO containing ticket details.
     * @return the ID of the created ticket.
     */
    String save(TicketCreateDTO ticketCreateDTO);

    /**
     * Updates an existing ticket.
     *
     * @param ticketUpdateDTO the DTO containing updated ticket details.
     * @return the ID of the updated ticket.
     */
    String update(TicketUpdateDTO ticketUpdateDTO);

    /**
     * Deletes a ticket by its ID.
     *
     * @param id the ID of the ticket to delete.
     * @return the ID of the deleted ticket.
     */
    String delete(String id);

    /**
     * Retrieves a ticket by its ID.
     *
     * @param ticketId the ID of the ticket to retrieve.
     * @return an {@link Optional} containing the {@link TicketResponseDTO}, if found.
     */
    Optional<TicketResponseDTO> getById(String ticketId);

    /**
     * Retrieves all tickets.
     *
     * @return a set of {@link TicketResponseDTO}.
     */
    Set<TicketResponseDTO> findAll();

    /**
     * Retrieves session details along with associated tickets.
     *
     * @param sessionId the ID of the session to retrieve.
     * @return the {@link FilmSessionResponseDTO} containing session and ticket details.
     */
    FilmSessionResponseDTO getSessionDetailsWithTickets(String sessionId);

    /**
     * Purchases a ticket.
     *
     * @param ticketCreateDTO the DTO containing ticket details.
     * @return the ID of the purchased ticket.
     */
    String purchaseTicket(TicketCreateDTO ticketCreateDTO);

    /**
     * Retrieves tickets by user ID.
     *
     * @param userId the ID of the user to retrieve tickets for.
     * @return a set of {@link TicketResponseDTO}.
     */
    Set<TicketResponseDTO> findByUserId(String userId);

    /**
     * Processes an action on a ticket.
     *
     * @param action the action to perform (e.g., "cancel").
     * @param ticketId the ID of the ticket.
     * @return the result of the action.
     */
    String processTicketAction(String action, Long ticketId);
}

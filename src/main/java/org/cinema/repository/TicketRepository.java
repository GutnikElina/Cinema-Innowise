package org.cinema.repository;

import org.cinema.model.Ticket;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing {@link Ticket} entities.
 * Provides methods for saving, retrieving, updating, and deleting tickets,
 * as well as finding tickets by specific criteria.
 */
public interface TicketRepository {

    /**
     * Saves a new ticket to the repository.
     *
     * @param ticket the {@link Ticket} entity to be saved.
     */
    void save(Ticket ticket);

    /**
     * Retrieves a ticket by its unique identifier.
     *
     * @param ticketId the ID of the ticket to retrieve.
     * @return an {@link Optional} containing the {@link Ticket} if found, or empty if not found.
     */
    Optional<Ticket> getById(long ticketId);

    /**
     * Retrieves all tickets in the repository.
     *
     * @return a {@link Set} of all {@link Ticket} entities.
     */
    Set<Ticket> findAll();

    /**
     * Updates an existing ticket in the repository with new purchase time details.
     *
     * @param ticket        the {@link Ticket} entity with updated details.
     * @param purchaseTime the new {@link LocalDateTime} of purchase.
     */
    void update(Ticket ticket, LocalDateTime purchaseTime);

    /**
     * Deletes a ticket from the repository by its unique identifier.
     *
     * @param ticketId the ID of the ticket to delete.
     */
    void delete(long ticketId);

    /**
     * Retrieves a list of tickets for a specific session.
     *
     * @param sessionId the ID of the session to retrieve tickets for.
     * @return a {@link List} of {@link Ticket} entities for the specified session.
     */
    List<Ticket> getTicketsBySession(long sessionId);

    /**
     * Checks if a ticket with the same details already exists in the repository.
     *
     * @param ticket the {@link Ticket} entity to check.
     * @return {@code true} if the ticket exists, {@code false} otherwise.
     */
    boolean checkIfTicketExists(Ticket ticket);

    /**
     * Retrieves a list of tickets purchased by a specific user.
     *
     * @param userId the ID of the user to retrieve tickets for.
     * @return a {@link List} of {@link Ticket} entities purchased by the specified user.
     */
    List<Ticket> getTicketsByUserId(long userId);
}

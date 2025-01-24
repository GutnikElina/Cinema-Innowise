package org.cinema.repository;

import org.cinema.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for managing {@link Ticket} entities.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Retrieves all tickets with their associated film sessions.
     *
     * @return a set of {@link Ticket} entities.
     */
    @Query("SELECT DISTINCT t FROM Ticket t JOIN FETCH t.filmSession fs " +
            "ORDER BY fs.date ASC, fs.startTime ASC, t.seatNumber ASC")
    Set<Ticket> findAllWithSessions();

    /**
     * Retrieves all tickets associated with a specific film session.
     *
     * @param sessionId the ID of the film session.
     * @return a list of {@link Ticket} entities.
     */
    @Query("SELECT t FROM Ticket t WHERE t.filmSession.id = :sessionId " +
            "ORDER BY t.seatNumber ASC")
    List<Ticket> findTicketsBySession(@Param("sessionId") long sessionId);

    /**
     * Checks if a ticket already exists for a specific seat in a film session.
     *
     * @param sessionId the ID of the film session.
     * @param seatNumber the seat number.
     * @return true if the ticket exists, otherwise false.
     */
    @Query("SELECT COUNT(t) > 0 FROM Ticket t WHERE t.filmSession.id = :sessionId " +
            "AND t.seatNumber = :seatNumber")
    boolean existsBySessionAndSeat(@Param("sessionId") long sessionId,
                                   @Param("seatNumber") int seatNumber);

    /**
     * Retrieves all tickets purchased by a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of {@link Ticket} entities.
     */
    @Query("SELECT t FROM Ticket t JOIN FETCH t.filmSession fs " +
            "WHERE t.user.id = :userId " +
            "ORDER BY fs.date ASC, fs.startTime ASC, t.seatNumber ASC")
    List<Ticket> findTicketsByUserId(@Param("userId") long userId);
}

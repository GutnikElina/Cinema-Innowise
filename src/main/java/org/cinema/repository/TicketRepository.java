package org.cinema.repository;

import org.cinema.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing {@link Ticket} entities.
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Retrieves all tickets associated with a specific film session.
     *
     * @param sessionId the ID of the film session.
     * @return a list of {@link Ticket} entities.
     */
    List<Ticket> findByFilmSessionId(Long sessionId);

    /**
     * Checks if a ticket already exists for a specific seat in a film session.
     *
     * @param sessionId the ID of the film session.
     * @param seatNumber the seat number.
     * @return true if the ticket exists, otherwise false.
     */
    boolean existsByFilmSessionIdAndSeatNumber(Long sessionId, String seatNumber);

    /**
     * Retrieves all tickets purchased by a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of {@link Ticket} entities.
     */
    @Query("SELECT t FROM Ticket t JOIN FETCH t.filmSession fs " +
            "WHERE t.user.id = :userId ")
    List<Ticket> findTicketsByUserId(@Param("userId") Long userId);
}

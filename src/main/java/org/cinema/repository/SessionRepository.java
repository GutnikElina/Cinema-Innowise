package org.cinema.repository;

import org.cinema.model.FilmSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository interface for managing {@link FilmSession} entities.
 */
@Repository
public interface SessionRepository extends JpaRepository<FilmSession, Long> {

    /**
     * Retrieves all film sessions for the given date.
     *
     * @param date the date for which film sessions are to be retrieved.
     * @return a list of {@link FilmSession} entities for the given date.
     */
    List<FilmSession> findByDate(LocalDate date);

    /**
     * Checks if a film session overlaps with an existing one.
     *
     * @param movieId   the ID of the movie.
     * @param date      the date of the film session.
     * @param startTime the start time of the film session.
     * @param endTime   the end time of the film session.
     * @return true if a conflicting session exists, otherwise false.
     */
    @Query("SELECT COUNT(fs) > 0 FROM FilmSession fs WHERE fs.movie.id = :movieId AND fs.date = :date " +
            "AND (:start < fs.endTime AND :end > fs.startTime)")
    boolean existsOverlappingSession(@Param("movieId") Long movieId,
                                     @Param("date") LocalDate date,
                                     @Param("start") LocalTime startTime,
                                     @Param("end") LocalTime endTime);

}
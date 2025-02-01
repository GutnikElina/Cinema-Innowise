package org.cinema.service;

import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import java.util.List;

/**
 * Service interface for managing film sessions.
 */
public interface SessionService {
    /**
     * Creates a new film session.
     *
     * @param createDTO the DTO containing the session details.
     * @param movieId the ID of the movie associated with the session.
     * @return the ID of the created session.
     */
    String save(FilmSessionCreateDTO createDTO, Long movieId);

    /**
     * Updates an existing film session.
     *
     * @param updateDTO the DTO containing updated session details.
     * @param movieId the ID of the associated movie.
     * @return the ID of the updated session.
     */
    String update(FilmSessionUpdateDTO updateDTO, Long movieId);

    /**
     * Deletes a film session by its ID.
     *
     * @param id the ID of the session to delete.
     * @return the ID of the deleted session.
     */
    String delete(String id);

    /**
     * Retrieves a film session by its ID.
     *
     * @param id the ID of the session to retrieve.
     * @return the corresponding {@link FilmSessionResponseDTO}.
     */
    FilmSessionResponseDTO getById(String id);

    /**
     * Retrieves all film sessions.
     *
     * @return a list of {@link FilmSessionResponseDTO}.
     */
    List<FilmSessionResponseDTO> findAll();

    /**
     * Retrieves film sessions by date.
     *
     * @param date the date to filter sessions by.
     * @return a list of {@link FilmSessionResponseDTO}.
     */
    List<FilmSessionResponseDTO> findByDate(String date);
}

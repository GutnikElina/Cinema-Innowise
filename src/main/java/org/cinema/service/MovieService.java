package org.cinema.service;

import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.model.Movie;
import java.util.List;
import java.util.Set;

public interface MovieService {
    List<MovieResponseDTO> findAll();
    List<MovieResponseDTO> searchMovies(String title);
}

package org.cinema.service;

import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;

import java.util.Set;

public interface SessionService {
    String save(FilmSessionCreateDTO createDTO, Long movieId);
    String update(FilmSessionUpdateDTO updateDTO, Long movieId);
    String delete(String id);
    FilmSessionResponseDTO getById(String id);
    Set<FilmSessionResponseDTO> findAll();
    Set<FilmSessionResponseDTO> findByDate(String date);
}

package org.cinema.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.mapper.filmSessionMapper.FilmSessionCreateMapper;
import org.cinema.mapper.filmSessionMapper.FilmSessionResponseMapper;
import org.cinema.mapper.filmSessionMapper.FilmSessionUpdateMapper;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.repository.MovieRepository;
import org.cinema.repository.SessionRepository;
import org.cinema.service.SessionService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final MovieRepository movieRepository;

    @Override
    @Transactional
    public String save(FilmSessionCreateDTO createDTO, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() ->
                new NoDataFoundException("Error! Movie with ID " + movieId + " doesn't exist!")
        );

        FilmSession filmSession = FilmSessionCreateMapper.INSTANCE.toEntity(createDTO);
        filmSession.setMovie(movie);

        if (sessionRepository.existsOverlappingSession(movieId, filmSession.getDate(),
                filmSession.getStartTime(), filmSession.getEndTime())) {
            throw new EntityAlreadyExistException("Film session already exists on this film and time. Try again.");
        }

        sessionRepository.save(filmSession);

        return "Film session successfully added.";
    }

    @Override
    @Transactional
    public String update(FilmSessionUpdateDTO updateDTO, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() ->
                new NoDataFoundException("Error! Movie with ID " + movieId + " doesn't exist!")
        );

        FilmSession filmSession = FilmSessionUpdateMapper.INSTANCE.toEntity(updateDTO);
        filmSession.setMovie(movie);

        if (sessionRepository.existsOverlappingSession(movieId, filmSession.getDate(),
                filmSession.getStartTime(), filmSession.getEndTime())) {
            throw new EntityAlreadyExistException("Film session already exists on this film and time. Try again.");
        }

        sessionRepository.save(filmSession);

        return "Film session successfully updated.";
    }

    @Override
    @Transactional
    public String delete(String id) {
        Long sessionId = ValidationUtil.parseLong(id);
        sessionRepository.deleteById(sessionId);
        return "Film session successfully deleted.";
    }

    @Override
    public FilmSessionResponseDTO getById(String id) {
        Long sessionId = ValidationUtil.parseLong(id);
        Optional<FilmSession> session = sessionRepository.findById(sessionId);
        return session.map(FilmSessionResponseMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NoDataFoundException("Film session not found."));
    }

    @Override
    public Set<FilmSessionResponseDTO> findAll() {
        Set<FilmSession> sessions = sessionRepository.findAll().stream().collect(Collectors.toSet());
        return sessions.stream()
                .map(FilmSessionResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<FilmSessionResponseDTO> findByDate(String dateStr) {
        ValidationUtil.validateDate(dateStr);
        Set<FilmSession> sessions = sessionRepository.findByDate(LocalDate.parse(dateStr));
        return sessions.stream()
                .map(FilmSessionResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toSet());
    }
}

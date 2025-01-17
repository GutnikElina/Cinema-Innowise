package org.cinema.service.impl;

import lombok.Getter;
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
import org.cinema.repository.impl.MovieRepositoryImpl;
import org.cinema.repository.impl.SessionRepositoryImpl;
import org.cinema.service.SessionService;
import org.cinema.util.ValidationUtil;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SessionServiceImpl implements SessionService {

    @Getter
    private static final SessionServiceImpl instance = new SessionServiceImpl();

    private final SessionRepositoryImpl sessionRepository = SessionRepositoryImpl.getInstance();
    private final MovieRepositoryImpl movieRepository = MovieRepositoryImpl.getInstance();

    @Override
    public String save(FilmSessionCreateDTO createDTO, Long movieId) {
        Movie movie = movieRepository.getById(movieId).orElseThrow(() ->
                new NoDataFoundException("Error! Movie with ID " + movieId + " doesn't exist!")
        );

        FilmSession filmSession = FilmSessionCreateMapper.INSTANCE.toEntity(createDTO);
        filmSession.setMovie(movie);

        if (sessionRepository.checkIfSessionExists(filmSession)) {
            throw new EntityAlreadyExistException("Film session already exists on this film and time. Try again.");
        }

        sessionRepository.save(filmSession);

        if (!sessionRepository.checkIfSessionExists(filmSession)) {
            throw new EntityAlreadyExistException("Film session not found in database after saving. Try again.");
        }
        return "Film session successfully added.";
    }

    @Override
    public String update(FilmSessionUpdateDTO updateDTO, Long movieId) {
        Movie movie = movieRepository.getById(movieId).orElseThrow(() ->
                new NoDataFoundException("Error! Movie with ID " + movieId + " doesn't exist!")
        );

        FilmSession filmSession = FilmSessionUpdateMapper.INSTANCE.toEntity(updateDTO);
        filmSession.setMovie(movie);

        sessionRepository.update(filmSession);

        if (!sessionRepository.checkIfSessionExists(filmSession)) {
            throw new EntityAlreadyExistException("Film session not found in database after updating. Try again.");
        }
        return "Film session successfully updated.";
    }

    @Override
    public String delete(String id) {
        sessionRepository.delete(ValidationUtil.parseLong(id));
        return "Film session successfully deleted.";
    }

    @Override
    public FilmSessionResponseDTO getById(String id) {
        Optional<FilmSession> session = sessionRepository.getById(ValidationUtil.parseLong(id));
        return session.map(FilmSessionResponseMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NoDataFoundException("Film session not found."));
    }

    @Override
    public Set<FilmSessionResponseDTO> findAll() {
        Set<FilmSession> sessions = sessionRepository.findAll();
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
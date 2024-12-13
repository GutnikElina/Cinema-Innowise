package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.FilmSessionDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.mapper.FilmSessionMapper;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.repository.impl.SessionRepositoryImpl;
import org.cinema.service.SessionService;
import org.cinema.util.ValidationUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SessionServiceImpl implements SessionService {

    @Getter
    private static final SessionServiceImpl instance = new SessionServiceImpl();

    private final SessionRepositoryImpl sessionRepository = SessionRepositoryImpl.getInstance();
    private final MovieServiceImpl movieService = MovieServiceImpl.getInstance();

    private final FilmSessionMapper filmSessionMapper = FilmSessionMapper.INSTANCE;

    @Override
    public String save(String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                       String capacityStr, String priceStr) {

        Movie movie = movieService.getMovie(movieTitle);
        FilmSessionDTO dto = FilmSessionDTO.fromStrings(movie.getTitle(), dateStr, startTimeStr,
                endTimeStr, capacityStr, priceStr);
        FilmSession filmSession = filmSessionMapper.toEntity(dto);

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
    public Set<FilmSessionDTO> findAll() {
        Set<FilmSession> sessions = sessionRepository.findAll();
        return sessions.stream()
                .map(filmSessionMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public FilmSessionDTO getById(String id) {
        int sessionId = ValidationUtil.parseId(id);

        Optional<FilmSession> session = sessionRepository.getById(sessionId);
        return session.map(filmSessionMapper::toDTO)
                .orElseThrow(() -> new NoDataFoundException("Film session not found."));
    }

    @Override
    public String update(String id, String movieTitle, String dateStr, String startTimeStr,
                         String endTimeStr, String capacityStr, String priceStr) {

        Movie movie = movieService.getMovie(movieTitle);
        FilmSessionDTO dto = FilmSessionDTO.fromStringsWithId(id, movie.getTitle(), dateStr, 
                                                            startTimeStr, endTimeStr, capacityStr, priceStr);
        FilmSession filmSession = filmSessionMapper.toEntity(dto);
        sessionRepository.update(filmSession);

        if (!sessionRepository.checkIfSessionExists(filmSession)) {
            throw new EntityAlreadyExistException("Film session not found in database after updating. Try again.");
        }
        return "Film session successfully updated.";
    }

    @Override
    public String delete(String id) {
        int sessionId = ValidationUtil.parseId(id);
        sessionRepository.delete(sessionId);
        return "Film session successfully deleted.";
    }

    @Override
    public Set<FilmSessionDTO> findByDate(String dateStr) {
        ValidationUtil.validateDate(dateStr);
        LocalDate date = LocalDate.parse(dateStr);
        Set<FilmSession> sessions = sessionRepository.findByDate(date);
        return sessions.stream()
                .map(filmSessionMapper::toDTO)
                .collect(Collectors.toSet());
    }
}
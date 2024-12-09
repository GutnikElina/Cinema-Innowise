package org.cinema.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.EntityAlreadyExistException;
import org.cinema.error.NoDataFoundException;
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

@Slf4j
public class SessionServiceImpl implements SessionService {

    @Getter
    private static final SessionServiceImpl instance = new SessionServiceImpl();

    private final SessionRepositoryImpl sessionRepository = SessionRepositoryImpl.getInstance();
    private final MovieServiceImpl movieService = MovieServiceImpl.getInstance();

    @Override
    public String save(String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                       String capacityStr, String priceStr) {
        ValidationUtil.validateDate(dateStr);
        ValidationUtil.validatePrice(priceStr);
        ValidationUtil.validateCapacity(capacityStr);

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        int capacity = Integer.parseInt(capacityStr);
        BigDecimal price = new BigDecimal(priceStr);

        Movie movie = movieService.getMovie(movieTitle);

        FilmSession filmSession = new FilmSession(0, movie.getTitle(), price, date,
                startTime, endTime, capacity, null);

        if (sessionRepository.checkIfSessionExists(filmSession)) {
            throw new EntityAlreadyExistException("Film session already exists on this film and time. Try again.");
        }

        sessionRepository.save(filmSession);

        if (sessionRepository.checkIfSessionExists(filmSession)) {

            throw new NoDataFoundException("Film session not found in database after adding. Try again.");
        }
        return "Success! Session was successfully added to the database!";
    }

    @Override
    public String update(String id, String movieTitle, String dateStr, String startTimeStr,
                         String endTimeStr, String capacityStr, String priceStr) {
        int filmSessionId = ValidationUtil.parseId(id);
        FilmSession existingFilmSession = sessionRepository.getById(filmSessionId).orElseThrow(() ->
                new NoDataFoundException("Session with this ID doesn't exist!"));
        ;

        ValidationUtil.validateDate(dateStr);
        ValidationUtil.validatePrice(priceStr);
        ValidationUtil.validateCapacity(capacityStr);

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        int capacity = Integer.parseInt(capacityStr);
        BigDecimal price = new BigDecimal(priceStr);

        Movie movie = movieService.getMovie(movieTitle);

        FilmSession filmSession = new FilmSession(filmSessionId, movie.getTitle(), price,
                date, startTime, endTime, capacity, null);

        if (sessionRepository.checkIfSessionExists(filmSession)) {
            throw new EntityAlreadyExistException("Film session already exists on this film and time. Try again.");
        }

        sessionRepository.update(filmSession);
        return "Success! Session was successfully updated in the database!";
    }

    @Override
    public String delete(String sessionIdStr) {
        int sessionId = ValidationUtil.parseId(sessionIdStr);
        ValidationUtil.validateIsPositive(sessionId);
        sessionRepository.delete(sessionId);
        return "Success! Film session was successfully deleted!";
    }

    @Override
    public Optional<FilmSession> getById(String sessionIdStr) {
        int sessionId = ValidationUtil.parseId(sessionIdStr);
        ValidationUtil.validateIsPositive(sessionId);
        return sessionRepository.getById(sessionId);
    }

    @Override
    public Set<FilmSession> findAll() {
        Set<FilmSession> filmSessions = sessionRepository.findAll();

        if (filmSessions.isEmpty()) {
            throw new NoDataFoundException("No film sessions found in the database.");
        }

        log.info("{} film sessions retrieved successfully.", filmSessions.size());
        return filmSessions;
    }
}
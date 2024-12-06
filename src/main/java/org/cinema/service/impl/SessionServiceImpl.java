package org.cinema.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.repository.SessionRepository;
import org.cinema.service.SessionService;
import org.cinema.util.OmdbApiUtil;
import org.cinema.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    public SessionServiceImpl() {
        this.sessionRepository = new SessionRepository();
    }

    @Override
    public List<FilmSession> findAll() {
        return sessionRepository.findAll();
    }

    @Override
    public Optional<FilmSession> findById(int id) {
        return sessionRepository.getById(id);
    }

    @Override
    public String save(String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                       String capacityStr, String priceStr) {
        try {
            ValidationUtil.validateDate(dateStr);
            ValidationUtil.validatePrice(priceStr);
            ValidationUtil.validateCapacity(capacityStr);

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            int capacity = Integer.parseInt(capacityStr);
            BigDecimal price = new BigDecimal(priceStr);

            Movie movie = OmdbApiUtil.getMovie(movieTitle);
            if (movie == null) {
                throw new IllegalArgumentException("Film with this title not found.");
            }

            FilmSession filmSession = new FilmSession(0, movie.getTitle(), price, date, startTime, endTime, capacity);
            sessionRepository.save(filmSession);

            return "Success! Session was successfully added to the database!";
        } catch (IllegalArgumentException e) {
            log.error("Validation error during adding session: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        }
    }

    @Override
    public String update(int id, String movieTitle, String dateStr, String startTimeStr, String endTimeStr,
                         String capacityStr, String priceStr) {
        try {
            ValidationUtil.validateDate(dateStr);
            ValidationUtil.validatePrice(priceStr);
            ValidationUtil.validateCapacity(capacityStr);

            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            int capacity = Integer.parseInt(capacityStr);
            BigDecimal price = new BigDecimal(priceStr);

            Movie movie = OmdbApiUtil.getMovie(movieTitle);
            if (movie == null) {
                throw new IllegalArgumentException("Film with this title not found.");
            }

            FilmSession filmSession = new FilmSession(id, movie.getTitle(), price, date, startTime, endTime, capacity);
            sessionRepository.update(filmSession);

            return "Success! Session was successfully updated in the database!";
        } catch (Exception e) {
            log.error("Error updating session: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        }
    }

    @Override
    public String delete(int id) {
        try {
            sessionRepository.delete(id);
            return "Success! Session was successfully deleted from the database!";
        } catch (Exception e) {
            log.error("Error deleting session: {}", e.getMessage(), e);
            return "Error! Unable to delete the session.";
        }
    }
}
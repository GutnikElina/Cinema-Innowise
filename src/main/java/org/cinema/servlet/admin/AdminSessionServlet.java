package org.cinema.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.repository.SessionRepository;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.util.OmdbApiUtil;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.cinema.util.ValidationUtil.*;

@Slf4j
@WebServlet("/admin/sessions")
public class AdminSessionServlet extends HttpServlet {

    private SessionRepository sessionRepository;

    @Override
    public void init() {
        sessionRepository = new SessionRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<FilmSession> filmSessions = Collections.emptyList();
        String action = request.getParameter("action");
        String message = "";

        try {
            if ("edit".equals(action)) {
                handleEditAction(request);
            }
            filmSessions = sessionRepository.getAll();
        } catch (Exception e) {
            log.error("Unexpected error in doGet method (catch AdminSessionServlet): {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }
        request.setAttribute("filmSessions", filmSessions);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/sessions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String message = "";

        try {
            message = switch (action) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", action);
                    yield "Error! Unknown action.";
                }
            };
        } catch (HibernateException e) {
            log.error("Hibernate error (catch AdminSessionServlet): ", e);
            message = "Occurred error while performing database operation.";
        } catch (RuntimeException e) {
            log.error("Unexpected RuntimeException error (catch AdminSessionServlet): {}", e.getMessage(), e);
            message = "Unexpected error occurred, please try again later.";
        } catch (Exception e) {
            log.error("Unknown error (catch AdminSessionServlet): {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        try {
            String movieTitle = request.getParameter("movieTitle");
            String dateStr = request.getParameter("date");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            String capacityStr = request.getParameter("capacity");
            String priceStr = request.getParameter("price");

            validateDate(dateStr);
            validatePrice(priceStr);
            validateCapacity(capacityStr);

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

            sessionRepository.add(filmSession);
            return "Success! Session was successfully added to the database!";
        } catch (IllegalArgumentException e) {
            log.error("Validation error during adding session: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        }
    }


    private String handleDeleteAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            sessionRepository.delete(id);
            return "Success! Session was successfully deleted from the database!";
        } catch (NumberFormatException e) {
            log.error("Invalid session ID format during delete: {}", e.getMessage(), e);
            return "Error! Invalid session ID format.";
        }
    }

    private String handleUpdateAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String movieTitle = request.getParameter("movieTitle");
            String dateStr = request.getParameter("date");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            String capacityStr = request.getParameter("capacity");
            String priceStr = request.getParameter("price");

            validateDate(dateStr);
            validatePrice(priceStr);
            validateCapacity(capacityStr);

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
        } catch (NumberFormatException e) {
            log.error("Invalid number format for fields: {}", e.getMessage(), e);
            return "Error! Invalid input for price, capacity, or other fields. Please check your data.";
        } catch (IllegalArgumentException e) {
            log.error("Error updating session: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return "An unexpected error occurred. Please try again.";
        }
    }

    private void handleEditAction(HttpServletRequest request) {
        try {
            int sessionId = Integer.parseInt(request.getParameter("id"));
            Optional<FilmSession> sessionToEditOpt = sessionRepository.getById(sessionId);
            sessionToEditOpt.ifPresent(session -> request.setAttribute("sessionToEdit", session));
        } catch (NumberFormatException e) {
            log.error("Invalid session ID format: {}", e.getMessage(), e);
            request.setAttribute("message", "Error! Invalid session ID format.");
        }
    }
}

package org.cinema.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dao.SessionDAO;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.model.User;
import org.cinema.util.OmdbApiUtil;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebServlet("/admin/sessions")
public class AdminSessionServlet extends HttpServlet {

    private SessionDAO sessionDAO;

    @Override
    public void init() {
        sessionDAO = new SessionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<FilmSession> filmSessions = sessionDAO.getAll();
        String action = request.getParameter("action");

        if ("edit".equals(action)) { handleEditAction(request); }
        request.setAttribute("filmSessions", filmSessions);
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
                    yield "Unknown action.";
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
        request.setAttribute("message", message);
        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        try {
            String movieTitle = request.getParameter("movieTitle");
            String dateStr = request.getParameter("date");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            double price = Double.parseDouble(request.getParameter("price"));

            Timestamp startTime = parseTimestamp(dateStr, startTimeStr);
            Timestamp endTime = parseTimestamp(dateStr, endTimeStr);
            Timestamp date = Timestamp.valueOf(dateStr + " 00:00:00");

            Movie movie = OmdbApiUtil.getMovie(movieTitle);
            if (movie == null) {
                throw new IllegalArgumentException("Film with this title not found.");
            }
            FilmSession filmSession = new FilmSession(0, movie.getTitle(), price, date,
                    startTime, endTime, capacity);

            sessionDAO.add(filmSession);
            return "Session was successfully added to the database!";
        } catch (NumberFormatException e) {
            log.error("Invalid number format for fields: {}", e.getMessage(), e);
            return "Invalid input for price, capacity, or other fields. Please check your data.";
        } catch (IllegalArgumentException e) {
            log.error("Error adding session: {}", e.getMessage(), e);
            return e.getMessage();
        }
    }

    private String handleDeleteAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            sessionDAO.delete(id);
            return "Session was successfully deleted from the database!";
        } catch (NumberFormatException e) {
            log.error("Invalid session ID format during delete: {}", e.getMessage(), e);
            return "Invalid session ID format.";
        }
    }

    private String handleUpdateAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String movieTitle = request.getParameter("movieTitle");
            String dateStr = request.getParameter("date");
            String startTimeStr = request.getParameter("startTime");
            String endTimeStr = request.getParameter("endTime");
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            double price = Double.parseDouble(request.getParameter("price"));

            Timestamp startTime = parseTimestamp(dateStr, startTimeStr);
            Timestamp endTime = parseTimestamp(dateStr, endTimeStr);
            Timestamp date = Timestamp.valueOf(dateStr + " 00:00:00");

            Movie movie = OmdbApiUtil.getMovie(movieTitle);
            if (movie == null) {
                throw new IllegalArgumentException("Film with this title not found.");
            }
            FilmSession filmSession = new FilmSession(id, movie.getTitle(), price, date,
                    startTime, endTime, capacity);

            sessionDAO.update(filmSession);
            return "Session was successfully updated in the database!";
        } catch (NumberFormatException e) {
            log.error("Invalid number format for fields: {}", e.getMessage(), e);
            return "Invalid input for price, capacity, or other fields. Please check your data.";
        } catch (IllegalArgumentException e) {
            log.error("Error updating session: {}", e.getMessage(), e);
            return e.getMessage();
        }
    }

    private Timestamp parseTimestamp(String dateStr, String timeStr) {
        try {
            String dateTimeStr = dateStr + " " + timeStr;
            return Timestamp.valueOf(dateTimeStr + ":00");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid time.");
        }
    }

    private void handleEditAction(HttpServletRequest request) {
        try {
            int sessionId = Integer.parseInt(request.getParameter("id"));
            Optional<FilmSession> sessionToEditOpt = sessionDAO.getById(sessionId);
            sessionToEditOpt.ifPresent(session -> request.setAttribute("sessionToEdit", session));
        } catch (NumberFormatException e) {
            log.error("Invalid session ID format: {}", e.getMessage(), e);
            request.setAttribute("message", "Invalid session ID format.");
            //request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}

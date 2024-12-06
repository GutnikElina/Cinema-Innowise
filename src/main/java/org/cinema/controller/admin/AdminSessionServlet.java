package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.service.SessionService;
import org.cinema.service.impl.SessionServiceImpl;
import org.cinema.util.OmdbApiUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import static org.cinema.util.ValidationUtil.*;

@Slf4j
@WebServlet("/admin/sessions")
public class AdminSessionServlet extends HttpServlet {

    private SessionService sessionService;

    @Override
    public void init() {
        sessionService = new SessionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<FilmSession> filmSessions = Collections.emptyList();
        String message = "";

        try {
            String action = request.getParameter("action");
            if ("edit".equals(action)) {
                handleEditAction(request);
            }
            filmSessions = sessionService.findAll();
        } catch (Exception e) {
            log.error("Unexpected error in doGet method: {}", e.getMessage(), e);
            message = "An unexpected error occurred.";
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
        } catch (Exception e) {
            log.error("Unexpected error in doPost method: {}", e.getMessage(), e);
            message = "An unexpected error occurred.";
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        String movieTitle = request.getParameter("movieTitle");
        String dateStr = request.getParameter("date");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String capacityStr = request.getParameter("capacity");
        String priceStr = request.getParameter("price");

        return sessionService.save(movieTitle, dateStr, startTimeStr, endTimeStr, capacityStr, priceStr);
    }

    private String handleDeleteAction(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        return sessionService.delete(id);
    }

    private String handleUpdateAction(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        String movieTitle = request.getParameter("movieTitle");
        String dateStr = request.getParameter("date");
        String startTimeStr = request.getParameter("startTime");
        String endTimeStr = request.getParameter("endTime");
        String capacityStr = request.getParameter("capacity");
        String priceStr = request.getParameter("price");

        return sessionService.update(id, movieTitle, dateStr, startTimeStr, endTimeStr, capacityStr, priceStr);
    }

    private void handleEditAction(HttpServletRequest request) {
        try {
            int sessionId = Integer.parseInt(request.getParameter("id"));
            sessionService.findById(sessionId).ifPresent(session -> request.setAttribute("sessionToEdit", session));
        } catch (NumberFormatException e) {
            log.error("Invalid session ID format: {}", e.getMessage(), e);
            request.setAttribute("message", "Error! Invalid session ID format.");
        }
    }

}

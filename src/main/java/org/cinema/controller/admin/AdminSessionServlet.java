package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.FilmSessionDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.service.SessionService;
import org.cinema.service.impl.SessionServiceImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@WebServlet(name = "AdminSessionServlet", urlPatterns = {"/admin/sessions"})
public class AdminSessionServlet extends HttpServlet {

    private SessionService sessionService;

    @Override
    public void init() {
        sessionService = SessionServiceImpl.getInstance();
        log.info("AdminSessionServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for film sessions...");

        Set<FilmSessionDTO> filmSessions = Collections.emptySet();
        String message = request.getParameter("message");
        String action = request.getParameter("action");

        try {
            if ("edit".equals(action)) {
                handleEditAction(request);
            }
            log.debug("Start to fetch sessions...");
            filmSessions = sessionService.findAll();
        } catch (IllegalArgumentException e) {
            message = "Error! " + e.getMessage();
            log.error("Validation error! {}", e.getMessage(), e);
        } catch (NoDataFoundException e) {
            message = "Error! " + e.getMessage();
            log.error("Error while doing film sessions operation: {}", e.getMessage(), e);
        }

        request.setAttribute("filmSessions", filmSessions);
        if (message != null && !message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/sessions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for film sessions...");

        String action = request.getParameter("action");
        String message = "";

        try {
            switch (action) {
                case "add" -> message = handleAddAction(request);
                case "edit" -> message = handleEditSubmitAction(request);
                case "delete" -> message = handleDeleteAction(request);
                default -> {
                    message = "Error! Unknown action.";
                    log.error("Unknown action: {}", action);
                }
            }
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during film sessions action {}: {}", action, message, e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            message = "Error! " + e.getMessage();
            log.error("Error during film sessions action {}: {}", action, e.getMessage(), e);
        } catch (OmdbApiException e) {
            message = "Failed to communicate with OMDB API. Please try again later.";
            log.error("API error during movie search: {}", e.getMessage(), e);
        } catch (Exception e) {
            message = "Unexpected error occurred during film sessions operation '" + action + "'";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        String encodedMessage = response.encodeRedirectURL(message);
        response.sendRedirect(request.getContextPath() + "/admin/sessions?message=" + encodedMessage);
    }

    private void handleEditAction(HttpServletRequest request) {
        String sessionId = request.getParameter("id");
        FilmSessionDTO session = sessionService.getById(sessionId);
        request.setAttribute("sessionToEdit", session);
    }

    private String handleAddAction(HttpServletRequest request) {
        String movieTitle = request.getParameter("movieTitle");
        String date = request.getParameter("date");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String capacity = request.getParameter("capacity");
        String price = request.getParameter("price");

        return sessionService.save(movieTitle, date, startTime, endTime, capacity, price);
    }

    private String handleEditSubmitAction(HttpServletRequest request) {
        String sessionId = request.getParameter("id");
        String movieTitle = request.getParameter("movieTitle");
        String date = request.getParameter("date");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String capacity = request.getParameter("capacity");
        String price = request.getParameter("price");

        return sessionService.update(sessionId, movieTitle, date, startTime, endTime, capacity, price);
    }

    private String handleDeleteAction(HttpServletRequest request) {
        String sessionId = request.getParameter("id");
        return sessionService.delete(sessionId);
    }
}

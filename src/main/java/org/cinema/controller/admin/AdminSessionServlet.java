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

    private static final String VIEW_PATH = "/WEB-INF/views/sessions.jsp";
    private static final String REDIRECT_PATH = "/admin/sessions";
    private static final String MESSAGE_PARAM = "message";

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

        try {
            String action = request.getParameter("action");
            if ("edit".equals(action)) {
                handleEditAction(request);
            }
            
            log.debug("Fetching all sessions...");
            Set<FilmSessionDTO> filmSessions = sessionService.findAll();
            request.setAttribute("filmSessions", filmSessions);
            
            String message = request.getParameter(MESSAGE_PARAM);
            if (message != null && !message.isEmpty()) {
                request.setAttribute(MESSAGE_PARAM, message);
            }
            
        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error for session operation", e);
        } catch (NoDataFoundException e) {
            handleError(request, "Error! " + e.getMessage(),
                    "No sessions found: {}", e, e.getMessage());
            request.setAttribute("filmSessions", Collections.emptySet());
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while fetching sessions",
                    "Unexpected error during sessions fetching: {}", e, e.getMessage());
            request.setAttribute("filmSessions", Collections.emptySet());
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for film sessions...");

        try {
            String action = request.getParameter("action");
            log.debug("Processing action: {}", action);

            String message = switch (action) {
                case "add" -> handleAddAction(request);
                case "edit" -> handleEditSubmitAction(request);
                case "delete" -> handleDeleteAction(request);
                default -> {
                    log.warn("Unknown action requested: {}", action);
                    yield "Unknown action requested";
                }
            };

            request.getSession().setAttribute(MESSAGE_PARAM, message);
            
        } catch (IllegalArgumentException e) {
            handleSessionError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error for session operation", e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            handleSessionError(request, "Error! " + e.getMessage(),
                    "Business error during session operation: {}", e, e.getMessage());
        } catch (OmdbApiException e) {
            handleSessionError(request, "Error! Failed to communicate with OMDB API. Please try again later.",
                    "OMDB API error during session operation", e);
        } catch (Exception e) {
            handleSessionError(request, "An unexpected error occurred while processing the session",
                    "Unexpected error during session operation: {}", e, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + REDIRECT_PATH);
    }

    private void handleEditAction(HttpServletRequest request) {
        String sessionId = request.getParameter("id");
        FilmSessionDTO session = sessionService.getById(sessionId);
        request.setAttribute("sessionToEdit", session);
    }

    private String handleAddAction(HttpServletRequest request) {
        return sessionService.save(
                getRequiredParameter(request, "movieTitle"),
                getRequiredParameter(request, "date"),
                getRequiredParameter(request, "startTime"),
                getRequiredParameter(request, "endTime"),
                getRequiredParameter(request, "capacity"),
                getRequiredParameter(request, "price")
        );
    }

    private String handleEditSubmitAction(HttpServletRequest request) {
        return sessionService.update(
                getRequiredParameter(request, "id"),
                getRequiredParameter(request, "movieTitle"),
                getRequiredParameter(request, "date"),
                getRequiredParameter(request, "startTime"),
                getRequiredParameter(request, "endTime"),
                getRequiredParameter(request, "capacity"),
                getRequiredParameter(request, "price")
        );
    }

    private String handleDeleteAction(HttpServletRequest request) {
        return sessionService.delete(getRequiredParameter(request, "id"));
    }

    private String getRequiredParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value.trim();
    }

    private void handleError(HttpServletRequest request, String userMessage,
            String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        request.setAttribute(MESSAGE_PARAM, userMessage);
    }

    private void handleSessionError(HttpServletRequest request, String userMessage,
            String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        request.getSession().setAttribute(MESSAGE_PARAM, userMessage);
    }
}

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
            handleError(request, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException e) {
            handleError(request, e.getMessage(), e);
            request.setAttribute("filmSessions", Collections.emptySet());
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while fetching sessions", e);
            request.setAttribute("filmSessions", Collections.emptySet());
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for film sessions...");

        String action = request.getParameter("action");
        String message = "";

        try {
            message = switch (action) {
                case "add" -> handleAddAction(request);
                case "edit" -> handleEditSubmitAction(request);
                case "delete" -> handleDeleteAction(request);
                default -> {
                    log.error("Unknown action: {}", action);
                    yield "Unknown action requested";
                }
            };
        } catch (IllegalArgumentException e) {
            log.warn("Validation error for action {}: {}", action, e.getMessage(), e);
            message = "Error! Invalid input: " + e.getMessage();
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            log.warn("Business error for action {}: {}", action, e.getMessage(), e);
            message = e.getMessage();
        } catch (OmdbApiException e) {
            log.error("OMDB API error: {}", e.getMessage(), e);
            message = "Failed to communicate with OMDB API. Please try again later.";
        } catch (Exception e) {
            log.error("Unexpected error for action {}: {}", action, e.getMessage(), e);
            message = "An unexpected error occurred. Please try again later.";
        }

        redirectWithMessage(request, response, message);
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
                getRequiredParameter(request,  "endTime"),
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
                getRequiredParameter(request,  "endTime"),
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

    private void handleError(HttpServletRequest request, String message, Exception e) {
        log.error(message + ": {}", e.getMessage(), e);
        request.setAttribute(MESSAGE_PARAM, message);
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response, String message) 
            throws IOException {
        String encodedMessage = response.encodeRedirectURL(message);
        response.sendRedirect(request.getContextPath() + REDIRECT_PATH + "?" + MESSAGE_PARAM + "=" + encodedMessage);
    }
}

package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.EntityAlreadyExistException;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.FilmSession;
import org.cinema.model.Ticket;
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
        log.debug("Handling GET request for get film sessions...");

        Set<FilmSession> filmSessions = Collections.emptySet();
        String message = "";
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
        } catch (Exception e) {
            message = "Unexpected error occurred during fetching film sessions";
            log.error("{}: {}", message, e.getMessage(), e);
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
        log.debug("Handling POST request for film session operations...");

        String action = request.getParameter("action");
        String message = "";
        try {
            log.debug("Start to handle action: {}...", action);
            message = switch (action) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", action);
                    yield "Error! Unknown action.";
                }
            };
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during film sessions action {}: {}", action, message, e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            message = e.getMessage();
            log.error("Error during film sessions action {}: {}", action, message, e);
        } catch (Exception e) {
            message = "Unexpected error occurred during film sessions operation '" + action + "'";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        return sessionService.save(request.getParameter("movieTitle"), request.getParameter("date"),
                request.getParameter("startTime"), request.getParameter("endTime"),
                request.getParameter("capacity"), request.getParameter("price"));
    }

    private String handleDeleteAction(HttpServletRequest request) {
        return sessionService.delete(request.getParameter("id"));
    }

    private String handleUpdateAction(HttpServletRequest request) {
        return sessionService.update(request.getParameter("id"), request.getParameter("movieTitle"), request.getParameter("date"),
                request.getParameter("startTime"), request.getParameter("endTime"),
                request.getParameter("capacity"), request.getParameter("price"));
    }

    private void handleEditAction(HttpServletRequest request) {
        FilmSession sessionToEdit = sessionService.getById(request.getParameter("id")).orElseThrow(() ->
                new NoDataFoundException("Film session with this ID doesn't exist!"));

        request.setAttribute("sessionToEdit", sessionToEdit);
    }

}

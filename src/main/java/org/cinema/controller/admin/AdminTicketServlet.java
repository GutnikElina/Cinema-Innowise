package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.EntityAlreadyExistException;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.Ticket;
import org.cinema.service.TicketService;
import org.cinema.service.impl.TicketServiceImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@WebServlet(name = "AdminTicketServlet", urlPatterns = {"/admin/tickets"})
public class AdminTicketServlet extends HttpServlet {

    private TicketService ticketService;

    @Override
    public void init() {
        ticketService = TicketServiceImpl.getInstance();
        log.info("AdminTicketServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for get tickets...");

        Set<Ticket> tickets = Collections.emptySet();
        String message = "";
        try {
            if ("edit".equals(request.getParameter("action"))) {
                handleEditAction(request);
            }
            tickets = ticketService.findAll();
        } catch (IllegalArgumentException e) {
            message = "Error! " + e.getMessage();
            log.error("Validation error! {}", e.getMessage(), e);
        } catch (NoDataFoundException e) {
            message = "Error! " + e.getMessage();
            log.error("Error while doing tickets fetching: {}", e.getMessage(), e);
        } catch (Exception e) {
            message = "Unexpected error occurred during fetching tickets";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        request.setAttribute("tickets", tickets);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/tickets.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for tickets operations...");

        String message = "";
        String action = request.getParameter("action");
        try {
            message = switch (action) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", request.getParameter("action"));
                    yield "Error! Unknown action.";
                }
            };
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during ticket action {}: {}", action, message, e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            message = e.getMessage();
            log.error("Error during tickets action {}: {}", action, message, e);
        } catch (Exception e) {
            message = "Unexpected error occurred during tickets operation '" + action + "'";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }

        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        return ticketService.save(request.getParameter("userId"), request.getParameter("sessionId"), request.getParameter("seatNumber"),
                request.getParameter("status"), request.getParameter("requestType"));
    }

    private String handleDeleteAction(HttpServletRequest request) {
        return ticketService.delete(request.getParameter("id"));
    }

    private String handleUpdateAction(HttpServletRequest request) {
        return ticketService.update(request.getParameter("id"), request.getParameter("userId"),
                request.getParameter("sessionId"), request.getParameter("seatNumber"),
                request.getParameter("status"), request.getParameter("requestType"));
    }

    private void handleEditAction(HttpServletRequest request) {
        Ticket ticketToEdit = ticketService.getById(request.getParameter("id")).orElseThrow(() ->
                new NoDataFoundException("Ticket with this ID doesn't exist!"));

        request.setAttribute("ticketToEdit", ticketToEdit);
    }
}
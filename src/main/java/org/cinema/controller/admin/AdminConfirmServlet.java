package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.repository.TicketRepository;
import org.cinema.model.*;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import static org.cinema.util.ValidationUtil.validateParameters;

@Slf4j
@WebServlet(name = "AdminConfirmServlet", urlPatterns = {"/admin/tickets/confirm"})
public class AdminConfirmServlet extends HttpServlet {

    private TicketRepository ticketRepository;

    @Override
    public void init() {
        ticketRepository = new TicketRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Ticket> tickets = Collections.emptyList();
        String message = "";

        try {
            tickets = ticketRepository.findAll();
        } catch (IllegalArgumentException e) {
            log.error("Error in doGet method (catch AdminTicketServlet): {}", e.getMessage(), e);
            message = "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error in doGet method (catch AdminTicketServlet): {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }

        request.setAttribute("tickets", tickets);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/confirmTickets.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");
        String ticketIdParam = request.getParameter("id");
        String message;

        try {
            validateParameters(action, ticketIdParam);
            int ticketId = Integer.parseInt(ticketIdParam);
            Ticket ticket = ticketRepository.getById(ticketId).orElseThrow(() ->
                    new IllegalArgumentException("Ticket with this ID doesn't exist!"));

            message = processAction(action, ticket);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            message = "Error! " + e.getMessage();
        } catch (HibernateException e) {
            log.error("Database error: {}", e.getMessage(), e);
            message = "An error occurred while performing database operation.";
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        doGet(request, response);
    }

    private String processAction(String action, Ticket ticket) {
        return switch (action) {
            case "confirm" -> confirmTicket(ticket);
            case "return" -> returnTicket(ticket);
            case "cancel" -> cancelTicket(ticket);
            default -> {
                log.warn("Unknown action: {}", action);
                yield "Error! Unknown action.";
            }
        };
    }

    private String confirmTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING && ticket.getRequestType() == RequestType.PURCHASE) {
            ticket.setStatus(Status.CONFIRMED);
            ticketRepository.update(ticket);
            return "Success! Ticket Confirmed!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String returnTicket(Ticket ticket) {
        if (ticket.getRequestType() == RequestType.RETURN) {
            ticket.setStatus(Status.RETURNED);
            ticketRepository.update(ticket);
            return "Success! Ticket Returned!";
        }
        return "Error! Invalid action for this ticket.";
    }

    private String cancelTicket(Ticket ticket) {
        if (ticket.getStatus() == Status.PENDING) {
            ticket.setStatus(Status.CANCELLED);
            ticketRepository.update(ticket);
            return "Success! Ticket Cancelled!";
        }
        return "Error! Invalid action for this ticket.";
    }
}

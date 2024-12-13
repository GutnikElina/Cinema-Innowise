package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.Ticket;
import org.cinema.service.TicketService;
import org.cinema.service.impl.TicketServiceImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@WebServlet(name = "MyTicketsServlet", urlPatterns = {"/user/tickets"})
public class MyTicketsServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/myTickets.jsp";
    private static final String MESSAGE_PARAM = "message";

    private TicketService ticketService;

    @Override
    public void init() {
        this.ticketService = TicketServiceImpl.getInstance();
        log.info("MyTicketsServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for user's tickets...");

        try {
            loadUserTickets(request);
        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(), e);
            setEmptyTickets(request);
        } catch (NoDataFoundException e) {
            handleError(request, e.getMessage(), e);
            setEmptyTickets(request);
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while loading tickets", e);
            setEmptyTickets(request);
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for ticket actions...");

        try {
            String action = getRequiredParameter(request, "action");
            String message = processTicketAction(action, request);
            request.setAttribute(MESSAGE_PARAM, message);

        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException e) {
            handleError(request, e.getMessage(), e);
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while processing ticket action", e);
        }

        doGet(request, response);
    }

    private void loadUserTickets(HttpServletRequest request) {
        Integer userId = getUserId(request.getSession());
        Set<Ticket> tickets = ticketService.findByUserId(userId.toString());
        request.setAttribute("tickets", tickets);
    }

    private Integer getUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in session");
        }
        return userId;
    }

    private String processTicketAction(String action, HttpServletRequest request) {
        if ("returnMyTicket".equals(action)) {
            String ticketId = getRequiredParameter(request, "id");
            return ticketService.processTicketAction(action, ticketId);
        } else {
            log.warn("Unknown action requested: {}", action);
            return "Error! Unknown action requested";
        }
    }

    private String getRequiredParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value.trim();
    }

    private void setEmptyTickets(HttpServletRequest request) {
        request.setAttribute("tickets", Collections.emptySet());
    }

    private void handleError(HttpServletRequest request, String message, Exception e) {
        log.error("{}: {}", message, e.getMessage(), e);
        request.setAttribute(MESSAGE_PARAM, message);
    }
}

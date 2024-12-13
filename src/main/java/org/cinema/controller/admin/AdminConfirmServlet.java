package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.*;
import org.cinema.service.TicketService;
import org.cinema.service.impl.TicketServiceImpl;
import org.cinema.util.ValidationUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@WebServlet(name = "AdminConfirmServlet", urlPatterns = {"/admin/tickets/confirm"})
public class AdminConfirmServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/confirmTickets.jsp";
    private TicketService ticketService;

    @Override
    public void init() {
        ticketService = TicketServiceImpl.getInstance();
        log.info("AdminConfirmServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for get tickets...");

        try {
            log.debug("Start to fetch tickets...");
            Set<Ticket> tickets = ticketService.findAll();
            request.setAttribute("tickets", tickets);
        } catch (NoDataFoundException e) {
            log.warn("No tickets found: {}", e.getMessage());
            request.setAttribute("tickets", Collections.emptySet());
            request.setAttribute("message", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred during tickets fetching: {}", e.getMessage(), e);
            request.setAttribute("tickets", Collections.emptySet());
            request.setAttribute("message", "An unexpected error occurred while fetching tickets");
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for confirm tickets...");

        try {
            String action = request.getParameter("action");
            String ticketIdParam = request.getParameter("id");

            ValidationUtil.validateRequest(action, ticketIdParam);

            log.debug("Processing action {} for ticket ID {}", action, ticketIdParam);
            String message = ticketService.processTicketAction(action, ticketIdParam);

            request.getSession().setAttribute("message", message);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage(), e);
            request.getSession().setAttribute("message", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred during ticket operation: {}", e.getMessage(), e);
            request.getSession().setAttribute("message", "An unexpected error occurred while processing the ticket");
        }

        response.sendRedirect(request.getContextPath() + "/admin/tickets/confirm");
    }
}

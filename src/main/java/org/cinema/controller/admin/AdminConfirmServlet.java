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
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@WebServlet(name = "AdminConfirmServlet", urlPatterns = {"/admin/tickets/confirm"})
public class AdminConfirmServlet extends HttpServlet {

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

        Set<Ticket> tickets = Collections.emptySet();
        String message = "";

        try {
            log.debug("Start to fetch tickets...");
            tickets = ticketService.findAll();
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
        request.getRequestDispatcher("/WEB-INF/views/confirmTickets.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for confirm tickets...");

        String action = request.getParameter("action");
        String ticketIdParam = request.getParameter("id");
        String message = "";;

        try {
            log.debug("Start to process action {}...", action);
            message = ticketService.processTicketAction(action, ticketIdParam);
        } catch (IllegalArgumentException e) {
            message = "Error! " + e.getMessage();
            log.error("Validation error: {}", e.getMessage(), e);
        } catch (Exception e) {
            String error = "Unexpected error occurred during ticket operation";
            log.error("{}: {}", error, e.getMessage(), e);
            message = error;
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        doGet(request, response);
    }
}

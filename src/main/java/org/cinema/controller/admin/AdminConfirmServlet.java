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
    private static final String MESSAGE_PARAM = "message";
    private static final String REDIRECT_PATH = "/admin/tickets/confirm";

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
            
            String message = request.getParameter(MESSAGE_PARAM);
            if (message != null && !message.isEmpty()) {
                request.setAttribute(MESSAGE_PARAM, message);
            }
            
        } catch (NoDataFoundException e) {
            handleError(request, "Error! " + e.getMessage(),
                    "No tickets found: {}", e, e.getMessage());
            request.setAttribute("tickets", Collections.emptySet());
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while fetching tickets",
                    "Unexpected error during tickets fetching: {}", e, e.getMessage());
            request.setAttribute("tickets", Collections.emptySet());
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

            ValidationUtil.validateParameters(action, ticketIdParam);

            log.debug("Processing action {} for ticket ID {}", action, ticketIdParam);
            String message = ticketService.processTicketAction(action, ticketIdParam);
            
            response.sendRedirect(request.getContextPath() + REDIRECT_PATH + "?" + MESSAGE_PARAM + "=" + 
                    response.encodeRedirectURL(message));
            return;
            
        } catch (IllegalArgumentException e) {
            handleSessionError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error during ticket confirmation", e);
        } catch (NoDataFoundException e) {
            handleSessionError(request, "Error! " + e.getMessage(),
                    "Business error during ticket confirmation: {}", e, e.getMessage());
        } catch (Exception e) {
            handleSessionError(request, "An unexpected error occurred while processing the ticket",
                    "Unexpected error during ticket confirmation: {}", e, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + REDIRECT_PATH);
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

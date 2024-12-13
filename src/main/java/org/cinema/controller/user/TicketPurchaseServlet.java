package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.FilmSessionDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.FilmSession;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.cinema.service.impl.SessionServiceImpl;
import org.cinema.service.impl.TicketServiceImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@WebServlet(name = "TicketPurchaseServlet", urlPatterns = {"/user/tickets/purchase"})
public class TicketPurchaseServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/purchase.jsp";
    private static final String MESSAGE_PARAM = "message";

    private TicketService ticketService;
    private SessionService sessionService;

    @Override
    public void init() {
        ticketService = TicketServiceImpl.getInstance();
        sessionService = SessionServiceImpl.getInstance();
        log.info("TicketPurchaseServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for ticket purchase...");

        try {
            String selectedDate = request.getParameter("date");
            Set<FilmSessionDTO> filmSessions;

            if (selectedDate == null || selectedDate.isEmpty()) {
                log.debug("No date selected, fetching all sessions");
                filmSessions = sessionService.findAll();
            } else {
                log.debug("Fetching sessions for date: {}", selectedDate);
                filmSessions = sessionService.findByDate(selectedDate);
                
                if (filmSessions.isEmpty()) {
                    handleError(request, "Error! No film sessions found for the selected date. Displaying all sessions.",
                            "No sessions found for date: {}", null, selectedDate);
                    filmSessions = sessionService.findAll();
                }
                request.setAttribute("selectedDate", selectedDate);
            }
            request.setAttribute("filmSessions", filmSessions);

            String sessionId = request.getParameter("sessionId");
            if (sessionId != null && !sessionId.trim().isEmpty()) {
                log.debug("Loading details for session ID: {}", sessionId);
                FilmSession selectedSession = ticketService.getSessionDetailsWithTickets(sessionId);
                request.setAttribute("selectedSession", selectedSession);
                request.setAttribute("sessionId", sessionId);
            }

            String message = request.getParameter(MESSAGE_PARAM);
            if (message != null && !message.isEmpty()) {
                request.setAttribute(MESSAGE_PARAM, message);
            }

        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error during session loading", e);
        } catch (NoDataFoundException e) {
            handleError(request, "Error! " + e.getMessage(),
                    "No data found: {}", e, e.getMessage());
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while loading sessions",
                    "Unexpected error during session loading: {}", e, e.getMessage());
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for ticket purchase...");

        try {
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            String sessionId = request.getParameter("sessionId");
            String seatNumber = request.getParameter("seatNumber");

            log.debug("Processing ticket purchase for user ID: {}, session ID: {}, seat: {}", 
                    userId, sessionId, seatNumber);

            String message = ticketService.purchaseTicket(String.valueOf(userId), sessionId, seatNumber);
            response.sendRedirect(request.getContextPath() + "/user/tickets/purchase?" + MESSAGE_PARAM + "=" +
                    response.encodeRedirectURL(message));
            return;

        } catch (IllegalArgumentException e) {
            handleSessionError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error during ticket purchase", e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            handleSessionError(request, "Error! " + e.getMessage(),
                    "Business error during ticket purchase: {}", e, e.getMessage());
        } catch (Exception e) {
            handleSessionError(request, "An unexpected error occurred while processing the purchase",
                    "Unexpected error during ticket purchase: {}", e, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/user/tickets/purchase");
    }

    private void handleError(HttpServletRequest request, String userMessage,
            String logMessage, Exception e, Object... logParams) {
        if (e != null) {
            log.error(logMessage, logParams, e);
        } else {
            log.warn(logMessage, logParams);
        }
        request.setAttribute(MESSAGE_PARAM, userMessage);
        request.setAttribute("filmSessions", Collections.emptySet());
        request.setAttribute("selectedSession", null);
    }

    private void handleSessionError(HttpServletRequest request, String userMessage,
            String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        request.getSession().setAttribute(MESSAGE_PARAM, userMessage);
    }
}

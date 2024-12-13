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

        String selectedDate = request.getParameter("date");
        Set<FilmSessionDTO> filmSessions = Collections.emptySet();

        try {
            if (selectedDate == null || selectedDate.isEmpty()) {
                filmSessions = sessionService.findAll();
            } else {
                filmSessions = sessionService.findByDate(selectedDate);
                if (filmSessions.isEmpty()) {
                    filmSessions = sessionService.findAll();
                    request.setAttribute("message", "Error! No film sessions found for the selected date. Displaying all sessions.");
                }
                request.setAttribute("selectedDate", selectedDate);
            }

            request.setAttribute("filmSessions", filmSessions);

            String sessionId = request.getParameter("sessionId");
            if (sessionId != null && !sessionId.trim().isEmpty()) {
                FilmSession selectedSession = ticketService.getSessionDetailsWithTickets(sessionId);
                request.setAttribute("selectedSession", selectedSession);
                request.setAttribute("sessionId", sessionId);
            }

        } catch (IllegalArgumentException e) {
            log.error("Validation error! {}", e.getMessage(), e);
            request.setAttribute("message", "Validation error: " + e.getMessage());
        } catch (NoDataFoundException e) {
            log.error("Error during fetching film sessions: {}", e.getMessage(), e);
            request.setAttribute("message", "Error! " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during fetching film sessions: {}", e.getMessage(), e);
            request.setAttribute("message", "Unexpected error loading data: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/purchase.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for ticket purchase.");
        String message = "";
        try {
            Integer userId = (Integer) request.getSession().getAttribute("userId");
            message = ticketService.purchaseTicket(String.valueOf(userId), request.getParameter("sessionId"),
                    request.getParameter("seatNumber"));
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during purchasing ticket: {}", message, e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            message = "Error!" + e.getMessage();
            log.error("Error during purchasing ticket: {}", e.getMessage(), e);
        } catch (Exception e) {
            message = "Unexpected error while purchasing ticket";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        request.setAttribute("message", message + " Please try again.");
        doGet(request, response);
    }
}

package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.FilmSession;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.cinema.service.impl.SessionServiceImpl;
import org.cinema.service.impl.TicketServiceImpl;

import java.io.IOException;
import java.util.List;

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
        log.info("Handling GET request for ticket purchase...");

        try {
            List<FilmSession> filmSessions = sessionService.findAll();
            request.setAttribute("filmSessions", filmSessions);

            String sessionIdStr = request.getParameter("sessionId");
            if (sessionIdStr != null) {
                int sessionId = Integer.parseInt(sessionIdStr);
                FilmSession selectedSession = ticketService.getSessionDetailsWithTickets(sessionId);
                request.setAttribute("selectedSession", selectedSession);
            }
        } catch (Exception e) {
            log.error("Error loading film sessions: {}", e.getMessage(), e);
            request.setAttribute("message", "Error loading data: " + e.getMessage());
        }
        request.getRequestDispatcher("/WEB-INF/views/purchase.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("Handling POST request for ticket purchase.");
        String message;
        try {
            int userId = (int) request.getSession().getAttribute("userId");
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            String seatNumber = request.getParameter("seatNumber");

            ticketService.purchaseTicket(userId, sessionId, seatNumber);
            message = "Success! Ticket purchased, awaiting confirmation.";
        } catch (Exception e) {
            log.error("Error purchasing ticket: {}", e.getMessage(), e);
            message = "Error purchasing ticket: " + e.getMessage();
        }

        request.setAttribute("message", message);
        doGet(request, response);
    }
}

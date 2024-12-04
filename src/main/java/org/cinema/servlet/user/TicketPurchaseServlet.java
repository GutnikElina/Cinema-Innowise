package org.cinema.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dao.SessionDAO;
import org.cinema.dao.TicketDAO;
import org.cinema.model.FilmSession;
import org.cinema.model.Ticket;
import org.cinema.model.User;
import org.cinema.model.Status;
import org.cinema.util.ValidationUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/user/tickets/purchase")
@Slf4j
public class TicketPurchaseServlet extends HttpServlet {

    private TicketDAO ticketDAO;
    private SessionDAO sessionDAO;

    @Override
    public void init() {
        ticketDAO = new TicketDAO();
        sessionDAO = new SessionDAO();
        log.info("TicketPurchaseServlet initialized with TicketDAO and SessionDAO.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("Handling GET request for ticket purchase...");

        try {
            List<FilmSession> filmSessions = sessionDAO.getAll();
            request.setAttribute("filmSessions", filmSessions);

            String sessionIdStr = request.getParameter("sessionId");
            if (sessionIdStr != null) {
                int sessionId = Integer.parseInt(sessionIdStr);
                FilmSession session = sessionDAO.getById(sessionId)
                        .orElseThrow(() -> new IllegalArgumentException("Session not found with ID: " + sessionId));

                List<Ticket> tickets = ticketDAO.getTicketsBySession(sessionId);
                List<Integer> takenSeats = tickets.stream()
                        .map(ticket -> Integer.parseInt(ticket.getSeatNumber()))
                        .toList();

                session.setTakenSeats(takenSeats);
                request.setAttribute("selectedSession", session);
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
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            String seatNumber = request.getParameter("seatNumber");
            log.info("Received POST data: sessionId={}, seatNumber={}", sessionId, seatNumber);

            FilmSession session = sessionDAO.getById(sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("Session not found with ID: " + sessionId));

            ValidationUtil.validateSeatNumber(seatNumber, session.getCapacity());
            log.info("Seat number {} validated successfully for session {}.", seatNumber, sessionId);

            Ticket ticket = new Ticket();
            ticket.setUser(new User());
            ticket.setFilmSession(session);
            ticket.setSeatNumber(seatNumber);
            ticket.setStatus(Status.PENDING);
            ticket.setPurchaseTime(LocalDateTime.now());

            ticketDAO.add(ticket);
            log.info("Ticket successfully created for session {} and seat {}.", sessionId, seatNumber);

            message = "Ticket purchased successfully! Awaiting confirmation.";
        } catch (Exception e) {
            log.error("Error purchasing ticket: {}", e.getMessage(), e);
            message = "Error purchasing ticket: " + e.getMessage();
        }

        request.setAttribute("message", message);
        doGet(request, response);
    }
}

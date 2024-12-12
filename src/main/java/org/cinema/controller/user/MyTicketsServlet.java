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
import java.util.Set;

@Slf4j
@WebServlet(name = "MyTicketsServlet", urlPatterns = {"/user/tickets"})
public class MyTicketsServlet extends HttpServlet {

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

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
//
//        if (userId == null) {
//            handleUnauthorizedAccess(request, response);
//            return;
//        }

        try {
            Set<Ticket> tickets = ticketService.findByUserId(userId.toString());
            request.setAttribute("tickets", tickets);
        } catch (IllegalArgumentException e) {
            log.error("Validation error! {}", e.getMessage(), e);
            request.setAttribute("message", "Validation error: " + e.getMessage());
        } catch (NoDataFoundException e) {
            log.error("Error during fetching tickets of user with ID {} : {}", userId, e.getMessage(), e);
            request.setAttribute("message", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during fetching tickets of user with ID {} : {}",
                    userId, e.getMessage(), e);
            request.setAttribute("message", "Unexpected error loading data: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/myTickets.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for ticket actions...");

        String action = request.getParameter("action");
        String message = "";

        try {
            if ("returnMyTicket".equals(action)) {
                message = ticketService.processTicketAction(action, request.getParameter("id"));
            } else {
                message = "Error! Unknown action.";
            }
        } catch (IllegalArgumentException e) {
            log.error("Validation error! {}", e.getMessage(), e);
            request.setAttribute("message", "Validation error: " + e.getMessage());
        } catch (NoDataFoundException e) {
            log.error("Error during operation with ticket: {}", e.getMessage(), e);
            request.setAttribute("message", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during operation with ticket: {}", e.getMessage(), e);
            request.setAttribute("message", "Unexpected error loading data: " + e.getMessage());
        }

        request.setAttribute("message", message);
        doGet(request, response);
    }

    private void handleUnauthorizedAccess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.warn("User is not logged in!");
        request.setAttribute("message", "Error! You must log in to see your tickets.");
        request.getRequestDispatcher("/login").forward(request, response);
    }
}

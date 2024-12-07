package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.*;
import org.cinema.service.TicketService;
import org.cinema.service.impl.TicketServiceImpl;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import static org.cinema.util.ValidationUtil.validateParameters;

@Slf4j
@WebServlet(name = "AdminConfirmServlet", urlPatterns = {"/admin/tickets/confirm"})
public class AdminConfirmServlet extends HttpServlet {

    private TicketService ticketService;

    @Override
    public void init() {
        ticketService = TicketServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Ticket> tickets = Collections.emptyList();
        String message = "";

        try {
            tickets = ticketService.findAll();
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String ticketIdParam = request.getParameter("id");
        String message;

        try {
            message = ticketService.processTicketAction(action, ticketIdParam);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            message = "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        doGet(request, response);
    }
}

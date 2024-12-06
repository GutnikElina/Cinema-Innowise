package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Ticket;
import org.cinema.model.User;
import org.cinema.service.TicketService;
import org.cinema.service.impl.TicketServiceImpl;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebServlet("/admin/tickets")
public class AdminTicketServlet extends HttpServlet {

    private TicketService ticketService;

    @Override
    public void init() {
        ticketService = new TicketServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Ticket> tickets = Collections.emptyList();
        String message = "";
        try {
            if ("edit".equals(request.getParameter("action"))) {
                handleEditAction(request);
            }
            tickets = ticketService.findAll();
        } catch (Exception e) {
            log.error("Unexpected error (catch AdminTicketServlet): {}", e.getMessage(), e);
            message = "An unexpected error occurred.";
        }

        request.setAttribute("tickets", tickets);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/tickets.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String message = "";
        try {
            message = switch (request.getParameter("action")) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", request.getParameter("action"));
                    yield "Error! Unknown action.";
                }
            };
        } catch (Exception e) {
            log.error("Unknown error (catch AdminUserServlet): {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }

        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        int userId = Integer.parseInt(request.getParameter("userId"));
        int sessionId = Integer.parseInt(request.getParameter("sessionId"));
        String seatNumber = request.getParameter("seatNumber");
        String status = request.getParameter("status");
        String requestType = request.getParameter("requestType");

        return ticketService.save(userId, sessionId, seatNumber, status, requestType);
    }

    private String handleDeleteAction(HttpServletRequest request) {
        int ticketId = Integer.parseInt(request.getParameter("id"));
        return ticketService.delete(ticketId);
    }

    private String handleUpdateAction(HttpServletRequest request) {
        int ticketId = Integer.parseInt(request.getParameter("id"));
        int userId = Integer.parseInt(request.getParameter("userId"));
        int sessionId = Integer.parseInt(request.getParameter("sessionId"));
        String seatNumber = request.getParameter("seatNumber");
        String status = request.getParameter("status");
        String requestType = request.getParameter("requestType");

        return ticketService.update(ticketId, userId, sessionId, seatNumber, status, requestType);
    }

    private void handleEditAction(HttpServletRequest request) {
        int ticketId = Integer.parseInt(request.getParameter("id"));
        Ticket ticketToEdit = ticketService.getById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket with this ID doesn't exist!"));

        request.setAttribute("ticketToEdit", ticketToEdit);
    }
}
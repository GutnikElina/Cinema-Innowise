package org.cinema.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dao.SessionDAO;
import org.cinema.dao.TicketDAO;
import org.cinema.dao.UserDAO;
import org.cinema.model.Ticket;
import org.cinema.model.User;
import org.cinema.model.FilmSession;
import org.cinema.model.Status;
import org.cinema.model.RequestType;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@WebServlet("/admin/tickets")
public class AdminTicketServlet extends HttpServlet {

    private TicketDAO ticketDAO;
    private UserDAO userDAO;
    private SessionDAO sessionDAO;

    @Override
    public void init() {
        ticketDAO = new TicketDAO();
        userDAO = new UserDAO();
        sessionDAO = new SessionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Ticket> tickets = Collections.emptyList();
        List<FilmSession> filmSessions = Collections.emptyList();
        List<User> users = Collections.emptyList();
        String action = request.getParameter("action");
        String message = "";

        try {
            if ("edit".equals(action)) {
                handleEditAction(request);
            }
            filmSessions = sessionDAO.getAll();
            tickets = ticketDAO.getAll();
            users = userDAO.getAll();
        } catch (IllegalArgumentException e) {
            log.error("Error in doGet method (catch AdminTicketServlet): {}", e.getMessage(), e);
            message = "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error in doGet method (catch AdminTicketServlet): {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }
        request.setAttribute("users", users);
        request.setAttribute("filmSessions", filmSessions);
        request.setAttribute("tickets", tickets);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/tickets.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String message = "";

        try {
            message = switch (action) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", action);
                    yield "Error! Unknown action.";
                }
            };
        } catch (HibernateException e) {
            log.error("Hibernate error (catch AdminTicketServlet): ", e);
            message = "Occurred error while performing database operation.";
        } catch (Exception e) {
            log.error("Unexpected error (catch AdminTicketServlet): ", e);
            message = "An unexpected error occurred.";
        }
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            String seatNumber = request.getParameter("seatNumber");
            String statusStr = request.getParameter("status");
            String requestTypeStr = request.getParameter("requestType");

            Status status = Status.valueOf(statusStr.toUpperCase());
            RequestType requestType = RequestType.valueOf(requestTypeStr.toUpperCase());

            User user = userDAO.getById(userId).orElseThrow(() -> new IllegalArgumentException("User with this ID doesn't exist!"));
            FilmSession filmSession = sessionDAO.getById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session with this ID doesn't exist!"));

            int seatNum = Integer.parseInt(seatNumber);
            if (seatNum > filmSession.getCapacity()) {
                return "Error! Your seat number exceeds the session's capacity! Try again.";
            }

            Ticket ticket = new Ticket(0, user, filmSession, seatNumber, null, status, requestType);
            ticketDAO.add(ticket);
            return "Success! Ticket was successfully added to the database!";
        } catch (IllegalArgumentException e) {
            log.error("Occurred error while adding ticket: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Error adding ticket: {}", e.getMessage(), e);
            return "Error! Failed to add ticket. Please check the entered data.";
        }
    }

    private String handleDeleteAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ticketDAO.delete(id);
            return "Success! Ticket was successfully deleted from the database!";
        } catch (NumberFormatException e) {
            log.error("Invalid ticket ID format during delete: {}", e.getMessage(), e);
            return "Error! Invalid ticket ID format.";
        }
    }

    private String handleUpdateAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int userId = Integer.parseInt(request.getParameter("userId"));
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            String seatNumber = request.getParameter("seatNumber");
            String statusStr = request.getParameter("status");
            String requestTypeStr = request.getParameter("requestType");

            Status status = Status.valueOf(statusStr.toUpperCase());
            RequestType requestType = RequestType.valueOf(requestTypeStr.toUpperCase());

            User user = userDAO.getById(userId).orElseThrow(() -> new IllegalArgumentException("User with this ID doesn't exist!"));
            FilmSession filmSession = sessionDAO.getById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session with this ID doesn't exist!"));

            int seatNum = Integer.parseInt(seatNumber);
            if (seatNum > filmSession.getCapacity()) {
                return "Error! Seat number exceeds the session's capacity.";
            }

            Ticket ticket = new Ticket(id, user, filmSession, seatNumber, null, status, requestType);
            ticketDAO.update(ticket);
            return "Success! Ticket was successfully updated in the database!";
        } catch (IllegalArgumentException e) {
            log.error("Occurred error while updating ticket: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Error updating ticket: {}", e.getMessage(), e);
            return "Error! Failed to update ticket. Please check the entered data.";
        }
    }

    private void handleEditAction(HttpServletRequest request) {
        try {
            int ticketId = Integer.parseInt(request.getParameter("id"));
            Ticket ticketToEdit = ticketDAO.getById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket with this ID doesn't exist!"));

            request.setAttribute("ticketToEdit", ticketToEdit);
        } catch (NumberFormatException e) {
            log.error("Invalid ticket ID format: {}", e.getMessage(), e);
            request.setAttribute("message", "Error! Invalid ticket ID format.");
        }
    }
}
package org.cinema.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cinema.dao.SessionDAO;
import org.cinema.model.Movie;
import org.cinema.model.Session;
import org.cinema.util.OmdbApiUtil;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@WebServlet("/admin/sessions")
public class AdminSessionServlet extends HttpServlet {

    private SessionDAO sessionDAO;

    @Override
    public void init() {
        sessionDAO = new SessionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Session> sessions = sessionDAO.getAll();
        String action = request.getParameter("action");

        if ("edit".equals(action)) {
            int sessionId = Integer.parseInt(request.getParameter("id"));
            Optional<Session> sessionToEdit = sessionDAO.getById(sessionId);
            request.setAttribute("sessionToEdit", sessionToEdit);
        }

        request.setAttribute("sessions", sessions);
        request.getRequestDispatcher("/WEB-INF/views/sessions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}

package org.cinema.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cinema.dao.SessionDAO;
import org.cinema.model.FilmSession;
import org.cinema.model.Movie;
import org.cinema.util.OmdbApiUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<FilmSession> filmSessions = sessionDAO.getAll();
        String action = request.getParameter("action");

        if ("edit".equals(action)) {
            int sessionId = Integer.parseInt(request.getParameter("id"));
            Optional<FilmSession> sessionToEditOpt = sessionDAO.getById(sessionId);
            sessionToEditOpt.ifPresent(session -> request.setAttribute("sessionToEdit", session));
        }

        request.setAttribute("filmSessions", filmSessions);
        request.getRequestDispatcher("/WEB-INF/views/sessions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String message = "";

        try {
            if ("add".equals(action) || "update".equals(action)) {

                String movieTitle = request.getParameter("movieTitle");
                double price = Double.parseDouble(request.getParameter("price"));
                String dateStr = request.getParameter("date");
                String startTimeStr = request.getParameter("startTime");
                String endTimeStr = request.getParameter("endTime");
                int capacity = Integer.parseInt(request.getParameter("capacity"));

                Timestamp startTime = parseTimestamp(dateStr, startTimeStr);
                Timestamp endTime = parseTimestamp(dateStr, endTimeStr);
                Timestamp date = Timestamp.valueOf(dateStr + " 00:00:00");

                Movie movie = OmdbApiUtil.getMovie(movieTitle);
                if (movie == null) {
                    throw new IllegalArgumentException("Фильм не найден.");
                }
                FilmSession filmSession = new FilmSession(0, movie.getTitle(), price, date,
                        startTime, endTime, capacity);

                if ("add".equals(action)) {
                    sessionDAO.add(filmSession);
                    message = "Сеанс успешно добавлен!";
                } else if ("update".equals(action)) {
                    filmSession.setId(Integer.parseInt(request.getParameter("id")));
                    sessionDAO.update(filmSession);
                    message = "Сеанс успешно обновлен!";
                }
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                sessionDAO.delete(id);
                message = "Сеанс успешно удален!";
            }
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        } catch (Exception e) {
            message = "Ошибка: " + e.getMessage();
        }
        request.setAttribute("message", message);
        doGet(request, response);
    }

    private Timestamp parseTimestamp(String dateStr, String timeStr) {
        try {
            String dateTimeStr = dateStr + " " + timeStr;
            return Timestamp.valueOf(dateTimeStr + ":00");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректное время.");
        }
    }
}

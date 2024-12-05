package org.cinema.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Movie;
import org.cinema.util.OmdbApiUtil;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebServlet("/admin")
public class AdminMainPageServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String movieTitle = request.getParameter("movieTitle");
        List<Movie> movies = Collections.emptyList();
        String message = "";

        try {
            if (movieTitle != null && !movieTitle.trim().isEmpty()) {
                movies = OmdbApiUtil.searchMovies(movieTitle.trim());
                if (movies.isEmpty()) {
                    message = "Error! No movies found for the title: " + movieTitle;
                    log.warn(message);
                }
            } else {
                message = "Error! Please provide a valid movie title.";
                log.warn("Movie title is missing or empty in the request.");
            }
        } catch (Exception e) {
            message = e.getMessage();
            log.error("Error during movie search for title '{}': {}", movieTitle, e.getMessage(), e);
        }
        request.setAttribute("movies", movies);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }
}
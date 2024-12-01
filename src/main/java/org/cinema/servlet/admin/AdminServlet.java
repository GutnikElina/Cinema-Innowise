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
public class AdminServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String movieTitle = request.getParameter("movieTitle");
        List<Movie> movies = Collections.emptyList();
        String message = null;

        try {
            if (movieTitle != null && !movieTitle.trim().isEmpty()) {
                movies = OmdbApiUtil.searchMovies(movieTitle.trim());
                if (movies.isEmpty()) {
                    message = "No movies found for the title: " + movieTitle;
                    log.warn(message);
                }
            } else {
                message = "Please provide a valid movie title.";
                log.warn("Movie title is missing or empty in the request.");
            }
        } catch (Exception e) {
            message = "An error occurred while searching for movies. Please try again later.";
            log.error("Error during movie search for title '{}': {}", movieTitle, e.getMessage(), e);
        }
        request.setAttribute("movies", movies);
        request.setAttribute("message", message);
        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }
}
package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.Movie;
import org.cinema.service.MovieService;
import org.cinema.service.impl.MovieServiceImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebServlet(name = "AdminMainPageServlet", urlPatterns = {"/admin"})
public class AdminMainPageServlet extends HttpServlet {

    private MovieService movieService;

    @Override
    public void init() {
        movieService = MovieServiceImpl.getInstance();
        log.info("AdminMainPageServlet initialized.");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        log.debug("Handling GET request for search movies...");

        String message = "";
        String movieTitle = request.getParameter("movieTitle");

        List<Movie> movies = Collections.emptyList();
        log.debug("Start to fetch movies with title {}...", movieTitle);
        try {
            movies = movieService.searchMovies(movieTitle.trim());
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during fetching movies: {}", message, e);
        } catch (NoDataFoundException e) {
            message = e.getMessage();
            log.error("Error during fetching movies: {}", message, e);
        } catch (Exception e) {
            message = e.getMessage();
            log.error("Error during movie search for title {}: {}", movieTitle, message, e);
        }

        request.setAttribute("movies", movies);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
    }
}
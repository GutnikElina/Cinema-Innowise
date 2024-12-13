package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.model.Movie;
import org.cinema.service.MovieService;
import org.cinema.service.impl.MovieServiceImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebServlet(name = "UserMainPageServlet", urlPatterns = {"/user"})
public class UserMainPageServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/user.jsp";
    private static final String MESSAGE = "message";
    private static final String MOVIES = "movies";

    private MovieService movieService;

    @Override
    public void init() {
        movieService = MovieServiceImpl.getInstance();
        log.info("UserMainPageServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for search movies...");

        try {
            String movieTitle = request.getParameter("movieTitle");
            
            if (movieTitle != null && !movieTitle.trim().isEmpty()) {
                log.debug("Start to fetch movies with title: {}", movieTitle);
                List<Movie> movies = movieService.searchMovies(movieTitle.trim());
                request.setAttribute(MOVIES, movies);
            } else {
                log.debug("No movie title provided or movie title is empty.");
                request.setAttribute(MOVIES, Collections.emptyList());
            }

            String message = request.getParameter(MESSAGE);
            if (message != null && !message.isEmpty()) {
                request.setAttribute(MESSAGE, message);
            }

        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error during movie search", e);
        } catch (NoDataFoundException e) {
            handleError(request, "Error! " + e.getMessage(),
                    "No movies found: {}", e, e.getMessage());
        } catch (OmdbApiException e) {
            handleError(request, "Error! Failed to communicate with OMDB API. Please try again later.",
                    "OMDB API error during movie search: {}", e, e.getMessage());
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while searching for movies",
                    "Unexpected error during movie search: {}", e, e.getMessage());
        }
        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    private void handleError(HttpServletRequest request, String userMessage,
            String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        request.setAttribute(MESSAGE, userMessage);
        request.setAttribute(MOVIES, Collections.emptyList());
    }
}

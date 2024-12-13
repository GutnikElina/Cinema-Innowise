package org.cinema.controller.admin;

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
import java.util.Optional;

@Slf4j
@WebServlet(name = "AdminMainPageServlet", urlPatterns = {"/admin"})
public class AdminMainPageServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/admin.jsp";
    private static final String MOVIE_TITLE_PARAM = "movieTitle";
    
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
        
        String movieTitle = Optional.ofNullable(request.getParameter(MOVIE_TITLE_PARAM))
                .map(String::trim)
                .filter(title -> !title.isEmpty())
                .orElse(null);

        if (movieTitle != null) {
            processMovieSearch(request, movieTitle);
        } else {
            log.debug("No movie title provided or movie title is empty");
            request.setAttribute("movies", Collections.emptyList());
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    private void processMovieSearch(HttpServletRequest request, String movieTitle) {
        try {
            log.debug("Searching for movies with title: {}", movieTitle);
            List<Movie> movies = movieService.searchMovies(movieTitle);
            request.setAttribute("movies", movies);
            
        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error for movie search", e);
                    
        } catch (NoDataFoundException e) {
            handleError(request, "Error! No movies found for title " + movieTitle,
                    "No movies found for title '{}':{}", e, movieTitle, e.getMessage());
            
        } catch (OmdbApiException e) {
            handleError(request, "Error! Failed to communicate with OMDB API. Please try again later.",
                    "OMDB API error for title '{}'", e, movieTitle);
                    
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while searching for movies", 
                    "Unexpected error during movie search for title '{}'", e, movieTitle);
        }
    }

    private void handleError(HttpServletRequest request, String userMessage, 
            String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        request.setAttribute("movies", Collections.emptyList());
        request.setAttribute("message", userMessage);
    }
}
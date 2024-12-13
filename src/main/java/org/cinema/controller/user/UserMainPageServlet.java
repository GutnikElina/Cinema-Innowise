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
import java.util.Optional;

@Slf4j
@WebServlet(name = "UserMainPageServlet", urlPatterns = {"/user"})
public class UserMainPageServlet extends HttpServlet {

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

        Optional<String> movieTitleOpt = Optional.ofNullable(request.getParameter("movieTitle"))
                .filter(title -> !title.trim().isEmpty());
        List<Movie> movies = Collections.emptyList();
        String message = "";

        if (movieTitleOpt.isPresent()) {
            String movieTitle = movieTitleOpt.get().trim();
            log.debug("Start to fetch movies with title {}...", movieTitle);
            try {
                movies = movieService.searchMovies(movieTitle.trim());
            } catch (IllegalArgumentException e) {
                message = "Validation error! " + e.getMessage();
                log.error("Validation error during fetching movies: {}", message, e);
            } catch (NoDataFoundException e) {
                message =  "Error! " + e.getMessage();
                log.error("Error during fetching movies: {}", message, e);
            } catch (OmdbApiException e) {
                message = "Failed to communicate with OMDB API. Please try again later.";
                log.error("API error during movie search for title '{}': {}", movieTitle, e.getMessage(), e);
            } catch (Exception e) {
                message = "Error! " + e.getMessage();
                log.error("Error during movie search for title '{}': {}", movieTitle, e.getMessage(), e);
            }
        } else {
            log.debug("No movie title provided or movie title is empty.");
        }

        request.setAttribute("movies", Optional.ofNullable(movies).orElse(List.of()));
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/user.jsp").forward(request, response);
    }
}

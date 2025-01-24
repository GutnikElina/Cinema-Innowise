package org.cinema.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.model.Movie;
import org.cinema.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminMainPageController {

    private final MovieService movieService;

    @Autowired
    public AdminMainPageController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public String showAdminPage(@RequestParam(value = "movieTitle", required = false) String movieTitle, Model model) {
        log.debug("Handling GET request for search movies...");

        if (movieTitle != null && !movieTitle.trim().isEmpty()) {
            processMovieSearch(movieTitle.trim(), model);
        } else {
            log.debug("No movie title provided or movie title is empty");
            model.addAttribute("movies", Collections.emptyList());
        }

        return "admin"; // JSP файл, например admin.jsp
    }

    private void processMovieSearch(String movieTitle, Model model) {
        try {
            log.debug("Searching for movies with title: {}", movieTitle);
            List<Movie> movies = movieService.searchMovies(movieTitle);
            model.addAttribute("movies", movies);

        } catch (IllegalArgumentException e) {
            handleError("Error! Invalid input: " + e.getMessage(),
                    "Validation error for movie search", e, model);

        } catch (NoDataFoundException e) {
            handleError("Error! No movies found for title " + movieTitle,
                    "No movies found for title '{}':{}", e, movieTitle, e.getMessage(), model);

        } catch (OmdbApiException e) {
            handleError("Error! Failed to communicate with OMDB API. Please try again later.",
                    "OMDB API error for title '{}'", e, movieTitle, model);

        } catch (Exception e) {
            handleError("An unexpected error occurred while searching for movies",
                    "Unexpected error during movie search for title '{}'", e, movieTitle, model);
        }
    }

    private void handleError(String userMessage, String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        model.addAttribute("movies", Collections.emptyList());
        model.addAttribute("message", userMessage);
    }
}

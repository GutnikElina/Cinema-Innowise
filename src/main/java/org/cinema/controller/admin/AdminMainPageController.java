package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.service.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMainPageController {

    private final MovieService movieService;

    @GetMapping
    public String showAdminPage(@RequestParam(value = "movieTitle", required = false) String movieTitle,
                                Model model, RedirectAttributes redirectAttributes) {
        log.debug("Handling GET request for admin page...");

        if (movieTitle != null && !movieTitle.trim().isEmpty()) {
            return processMovieSearch(movieTitle.trim(), model, redirectAttributes);
        } else {
            log.debug("No movie title provided or movie title is empty");
            model.addAttribute("movies", Collections.emptyList());
        }

        return "admin";
    }

    private String processMovieSearch(String movieTitle, Model model, RedirectAttributes redirectAttributes) {
        try {
            log.debug("Searching for movies with title: {}", movieTitle);
            List<MovieResponseDTO> movies = movieService.searchMovies(movieTitle);
            model.addAttribute("movies", movies);

        } catch (IllegalArgumentException e) {
            handleError(redirectAttributes, "Error! Invalid input: " + e.getMessage(),
                    "Validation error for movie search", e);
            model.addAttribute("movies", Collections.emptyList());

        } catch (NoDataFoundException e) {
            handleError(redirectAttributes, "Error! No movies found for title " + movieTitle,
                    "No movies found for title '{}':{}", e, movieTitle);
            model.addAttribute("movies", Collections.emptyList());

        } catch (OmdbApiException e) {
            handleError(redirectAttributes, "Error! Failed to communicate with OMDB API. Please try again later.",
                    "OMDB API error for title '{}'", e, movieTitle);
            model.addAttribute("movies", Collections.emptyList());

        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while searching for movies",
                    "Unexpected error during movie search for title '{}'", e, movieTitle);
            model.addAttribute("movies", Collections.emptyList());
        }

        return "admin";
    }

    private void handleError(RedirectAttributes redirectAttributes, String userMessage, String logMessage,
                             Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        redirectAttributes.addFlashAttribute("movies", Collections.emptyList());
        redirectAttributes.addFlashAttribute("message", userMessage);
    }
}
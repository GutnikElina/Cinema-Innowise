package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserMainPageController {

    private final MovieService movieService;

    @GetMapping()
    public String getUserMainPage(@RequestParam(value = "movieTitle", required = false) String movieTitle,
                                  @RequestParam(value = "message", required = false) String message,
                                  Model model) {
        log.debug("Handling GET request for search movies...");

        try {
            if (movieTitle != null && !movieTitle.trim().isEmpty()) {
                log.debug("Start to fetch movies with title: {}", movieTitle);
                List<MovieResponseDTO> movies = movieService.searchMovies(movieTitle.trim());
                model.addAttribute("movies", movies);
            } else {
                log.debug("No movie title provided or movie title is empty.");
                model.addAttribute("movies", Collections.emptyList());
            }

            if (message != null && !message.isEmpty()) {
                model.addAttribute("message", message);
            }

        } catch (IllegalArgumentException e) {
            handleError(model, "Error! Invalid input: " + e.getMessage(),
                    "Validation error during movie search", e);
        } catch (NoDataFoundException e) {
            handleError(model, "Error! " + e.getMessage(),
                    "No movies found: {}", e, e.getMessage());
        } catch (OmdbApiException e) {
            handleError(model, "Error! Failed to communicate with OMDB API. Please try again later.",
                    "OMDB API error during movie search: {}", e, e.getMessage());
        } catch (Exception e) {
            handleError(model, "An unexpected error occurred while searching for movies",
                    "Unexpected error during movie search: {}", e, e.getMessage());
        }
        return "user";
    }

    @GetMapping("/search")
    public String searchMovies(@RequestParam(value = "movieTitle", required = false) String movieTitle,
                               RedirectAttributes redirectAttributes) {
        try {
            if (movieTitle != null && !movieTitle.trim().isEmpty()) {
                log.debug("Start to fetch movies with title: {}", movieTitle);
                List<MovieResponseDTO> movies = movieService.searchMovies(movieTitle.trim());
                redirectAttributes.addFlashAttribute("movies", movies);
            } else {
                log.debug("No movie title provided or movie title is empty.");
                redirectAttributes.addFlashAttribute("movies", Collections.emptyList());
            }
            redirectAttributes.addFlashAttribute("message", "Search completed");

        } catch (IllegalArgumentException e) {
            log.error("Validation error during movie search", e);
            redirectAttributes.addFlashAttribute("message", "Error! Invalid input: " + e.getMessage());
        } catch (NoDataFoundException e) {
            log.error("No movies found", e);
            redirectAttributes.addFlashAttribute("message", "Error! " + e.getMessage());
            redirectAttributes.addFlashAttribute("movies", Collections.emptyList());
        } catch (OmdbApiException e) {
            log.error("OMDB API error during movie search", e);
            redirectAttributes.addFlashAttribute("message", "Error! Failed to communicate with OMDB API. Please try again later.");
            redirectAttributes.addFlashAttribute("movies", Collections.emptyList());
        } catch (Exception e) {
            log.error("Unexpected error during movie search", e);
            redirectAttributes.addFlashAttribute("message", "An unexpected error occurred while searching for movies");
            redirectAttributes.addFlashAttribute("movies", Collections.emptyList());
        }
        return "redirect:/user";
    }

    private void handleError(Model model, String userMessage, String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        model.addAttribute("message", userMessage);
        model.addAttribute("movies", Collections.emptyList());
    }
}
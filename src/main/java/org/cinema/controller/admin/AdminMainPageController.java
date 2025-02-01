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
import org.apache.commons.lang3.StringUtils;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminMainPageController {

    private final MovieService movieService;

    @GetMapping
    public String showAdminPage(@RequestParam(value = "movieTitle", required = false) String movieTitle, Model model) {
        log.debug("Handling GET request for admin page...");

        if (StringUtils.isBlank(movieTitle)) {
            log.debug("No movie title provided.");
            model.addAttribute("movies", Collections.emptyList());
        } else {
            return processMovieSearch(movieTitle.trim(), model);
        }
        return "admin";
    }

    private String processMovieSearch(String movieTitle, Model model) {
        try {
            log.debug("Searching for movies with title: {}", movieTitle);
            List<MovieResponseDTO> movies = movieService.searchMovies(movieTitle);
            model.addAttribute("movies", movies);
        } catch (IllegalArgumentException e) {
            handleError(model, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException e) {
            handleError(model, "Error! No movies found for title: " + movieTitle, e);
        } catch (OmdbApiException e) {
            handleError(model, "Error! Failed to communicate with OMDB API. Please try again later.", e);
        } catch (Exception e) {
            handleError(model, "An unexpected error occurred while searching for movies", e);
        }
        return "admin";
    }

    private void handleError(Model model, String userMessage, Exception e) {
        log.error(userMessage, e);
        model.addAttribute("movies", Collections.emptyList());
        model.addAttribute("message", userMessage);
    }
}
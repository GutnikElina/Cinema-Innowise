package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.constants.PageConstant;
import org.cinema.constants.ParamConstant;
import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.handler.ErrorHandler;
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
    public String showAdminPage(@RequestParam(value = ParamConstant.MOVIE_TITLE_PARAM, required = false) String movieTitle,
                                Model model) {
        log.debug("Handling GET request for admin page...");

        if (StringUtils.isBlank(movieTitle)) {
            log.debug("No movie title provided.");
            model.addAttribute(ParamConstant.MOVIES_PARAM, Collections.emptyList());
        } else {
            return processMovieSearch(movieTitle.trim(), model);
        }
        return PageConstant.ADMIN_PAGE;
    }

    private String processMovieSearch(String movieTitle, Model model) {
        try {
            log.debug("Searching for movies with title: {}", movieTitle);
            List<MovieResponseDTO> movies = movieService.searchMovies(movieTitle);
            model.addAttribute(ParamConstant.MOVIES_PARAM, movies);
        } catch (Exception e) {
            ErrorHandler.handleError(model, e);
        }
        return PageConstant.ADMIN_PAGE;
    }
}
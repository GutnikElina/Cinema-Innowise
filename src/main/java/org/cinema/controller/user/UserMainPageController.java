package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.service.MovieService;
import org.cinema.handler.ErrorHandler;
import org.cinema.util.ConstantsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserMainPageController {

    private final MovieService movieService;

    @GetMapping
    public String getUserMainPage(@RequestParam(value = ConstantsUtil.MOVIE_TITLE_PARAM, required = false) String movieTitle,
                                  @RequestParam(value = ConstantsUtil.MESSAGE_PARAM, required = false) String message,
                                  Model model) {
        log.debug("Handling GET request for user main page...");

        if (StringUtils.isBlank(movieTitle)) {
            log.debug("No movie title provided.");
            model.addAttribute(ConstantsUtil.MOVIES_PARAM, Collections.emptyList());
        } else {
            return processMovieSearch(movieTitle.trim(), model);
        }

        if (StringUtils.isNotBlank(message)) {
            model.addAttribute(ConstantsUtil.MESSAGE_PARAM, message);
        }
        return ConstantsUtil.USER_PAGE;
    }

    private String processMovieSearch(String movieTitle, Model model) {
        try {
            log.debug("Searching for movies with title: {}", movieTitle);
            List<MovieResponseDTO> movies = movieService.searchMovies(movieTitle);
            model.addAttribute(ConstantsUtil.MOVIES_PARAM, movies);
        } catch (Exception e) {
            ErrorHandler.handleError(model, e);
        }
        return ConstantsUtil.USER_PAGE;
    }
}
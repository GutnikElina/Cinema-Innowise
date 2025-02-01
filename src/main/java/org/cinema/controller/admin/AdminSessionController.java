package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.service.MovieService;
import org.cinema.service.SessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/admin/sessions")
@RequiredArgsConstructor
@Validated
public class AdminSessionController {

    private static final String MESSAGE_PARAM = "message";
    private static final String ID_PARAM = "id";
    private static final String SESSION_TO_EDIT = "sessionToEdit";
    private static final String FILM_SESSION_PARAM = "filmSessions";
    private static final String MOVIES_PARAM = "movies";
    private static final String SESSION_PAGE = "sessions";
    private static final String URL_REDIRECT = "redirect:/admin/sessions";

    private final SessionService sessionService;
    private final MovieService movieService;

    @GetMapping
    public String getSessions(Model model) {
        log.debug("Fetching all sessions...");
        model.addAttribute(FILM_SESSION_PARAM, sessionService.findAll());
        model.addAttribute(MOVIES_PARAM, movieService.findAll());
        return SESSION_PAGE;
    }

    @GetMapping("/edit")
    public String getEditSession(@RequestParam(ID_PARAM) String sessionId, Model model) {
        try {
            FilmSessionResponseDTO sessionToEdit = sessionService.getById(sessionId);
            model.addAttribute(SESSION_TO_EDIT, sessionToEdit);
            model.addAttribute(MOVIES_PARAM, movieService.findAll());
            model.addAttribute(FILM_SESSION_PARAM, sessionService.findAll());
        } catch (NoDataFoundException e) {
            log.error("Session not found: {}", e.getMessage());
            model.addAttribute(MESSAGE_PARAM, "Error! Session not found.");
        }
        return SESSION_PAGE;
    }

    @PostMapping("/add")
    public String addSession(@Valid @ModelAttribute FilmSessionCreateDTO createDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            String message = sessionService.save(createDTO, createDTO.getMovieId());
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        } catch (Exception e) {
            handleError(redirectAttributes, resolveErrorMessage(e), e);
        }
        return URL_REDIRECT;
    }

    @PostMapping("/edit")
    public String editSession(@Valid @ModelAttribute FilmSessionUpdateDTO updateDTO,
                              RedirectAttributes redirectAttributes) {
        try {
            String message = sessionService.update(updateDTO, updateDTO.getMovieId());
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        } catch (Exception e) {
            handleError(redirectAttributes, resolveErrorMessage(e), e);
        }
        return URL_REDIRECT;
    }

    @PostMapping("/delete")
    public String deleteSession(@RequestParam(ID_PARAM) String sessionId,
                                RedirectAttributes redirectAttributes) {
        try {
            String message = sessionService.delete(sessionId);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        } catch (Exception e) {
            handleError(redirectAttributes, resolveErrorMessage(e), e);
        }
        return URL_REDIRECT;
    }

    private void handleError(RedirectAttributes redirectAttributes, String userMessage, Exception e) {
        log.error(userMessage, e);
        redirectAttributes.addFlashAttribute(MESSAGE_PARAM, userMessage);
    }

    private String resolveErrorMessage(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return "Error! Invalid input: " + e.getMessage();
        } else if (e instanceof NoDataFoundException) {
            return "Error! No data found. " + e.getMessage();
        } else if (e instanceof EntityAlreadyExistException) {
            return e.getMessage();
        } else if (e instanceof OmdbApiException) {
            return "Error! Failed to communicate with OMDB API. Please try again later.";
        } else {
            return "An unexpected error occurred during working with sessions";
        }
    }
}

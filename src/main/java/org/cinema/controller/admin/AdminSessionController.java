package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.handler.ErrorHandler;
import org.cinema.service.MovieService;
import org.cinema.service.SessionService;
import org.cinema.util.ConstantsUtil;
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

    private final SessionService sessionService;
    private final MovieService movieService;

    @GetMapping
    public String getSessions(Model model) {
        log.debug("Fetching all sessions...");
        model.addAttribute(ConstantsUtil.FILM_SESSION_PARAM, sessionService.findAll());
        model.addAttribute(ConstantsUtil.MOVIES_PARAM, movieService.findAll());
        return ConstantsUtil.SESSION_PAGE;
    }

    @GetMapping("/edit")
    public String getEditSession(@RequestParam(ConstantsUtil.ID_PARAM) String sessionId, Model model) {
        try {
            FilmSessionResponseDTO sessionToEdit = sessionService.getById(sessionId);
            model.addAttribute(ConstantsUtil.SESSION_TO_EDIT, sessionToEdit);
            model.addAttribute(ConstantsUtil.MOVIES_PARAM, movieService.findAll());
            model.addAttribute(ConstantsUtil.FILM_SESSION_PARAM, sessionService.findAll());
        } catch (NoDataFoundException e) {
            log.error("Session not found: {}", e.getMessage());
            model.addAttribute(ConstantsUtil.MESSAGE_PARAM, "Error! Session not found.");
        }
        return ConstantsUtil.SESSION_PAGE;
    }

    @PostMapping("/add")
    public String addSession(@Valid @ModelAttribute FilmSessionCreateDTO createDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            String message = sessionService.save(createDTO, createDTO.getMovieId());
            redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return ConstantsUtil.REDIRECT_ADMIN_SESSIONS;
    }

    @PostMapping("/edit")
    public String editSession(@Valid @ModelAttribute FilmSessionUpdateDTO updateDTO,
                              RedirectAttributes redirectAttributes) {
        try {
            String message = sessionService.update(updateDTO, updateDTO.getMovieId());
            redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return ConstantsUtil.REDIRECT_ADMIN_SESSIONS;
    }

    @PostMapping("/delete")
    public String deleteSession(@RequestParam(ConstantsUtil.ID_PARAM) String sessionId,
                                RedirectAttributes redirectAttributes) {
        try {
            String message = sessionService.delete(sessionId);
            redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return ConstantsUtil.REDIRECT_ADMIN_SESSIONS;
    }
}

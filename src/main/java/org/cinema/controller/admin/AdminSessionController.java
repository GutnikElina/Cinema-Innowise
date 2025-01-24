package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionCreateDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.filmSessionDTO.FilmSessionUpdateDTO;
import org.cinema.dto.movieDTO.MovieResponseDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.exception.OmdbApiException;
import org.cinema.service.MovieService;
import org.cinema.service.SessionService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/admin/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final SessionService sessionService;
    private final MovieService movieService;

    @GetMapping
    public String getSessions(@RequestParam(value = "action", required = false) String action,
                              @RequestParam(value = "id", required = false) String sessionId,
                              Model model, RedirectAttributes redirectAttributes) {
        log.debug("Handling GET request for film sessions...");
        try {
            if ("edit".equals(action) && sessionId != null) {
                handleEditAction(sessionId, model);
            }

            log.debug("Fetching all sessions...");
            Set<FilmSessionResponseDTO> filmSessions = sessionService.findAll();
            model.addAttribute("filmSessions", filmSessions);

            List<MovieResponseDTO> movies = movieService.findAll();
            model.addAttribute("movies", movies);

        } catch (IllegalArgumentException e) {
            handleError(redirectAttributes, "Error! Invalid input: " + e.getMessage(),
                    "Validation error for session operation", e);
        } catch (NoDataFoundException e) {
            handleError(redirectAttributes, "Error! No sessions found.",
                    "No sessions found: {}", e, e.getMessage());
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while fetching sessions",
                    "Unexpected error during sessions fetching: {}", e, e.getMessage());
        }
        return "sessions";
    }

    @PostMapping
    public String handleSessionActions(@RequestParam("action") String action,
                                       @RequestParam Map<String, String> params,
                                       RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for film sessions...");
        try {
            String message = switch (action) {
                case "add" -> handleAddAction(params);
                case "edit" -> handleEditSubmitAction(params);
                case "delete" -> handleDeleteAction(params.get("id"));
                default -> {
                    log.warn("Unknown action requested: {}", action);
                    yield "Unknown action requested";
                }
            };
            redirectAttributes.addFlashAttribute("message", message);

        } catch (IllegalArgumentException e) {
            handleError(redirectAttributes, "Error! Invalid input: " + e.getMessage(),
                    "Validation error for session operation", e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            handleError(redirectAttributes, "Error! " + e.getMessage(),
                    "Business error during session operation: {}", e, e.getMessage());
        } catch (OmdbApiException e) {
            handleError(redirectAttributes, "Error! Failed to communicate with OMDB API. Please try again later.",
                    "OMDB API error during session operation", e);
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while processing the session",
                    "Unexpected error during session operation: {}", e, e.getMessage());
        }
        return "redirect:/admin/sessions";
    }

    private void handleEditAction(String sessionId, Model model) {
        log.debug("Handling edit action for session ID: {}", sessionId);
        if (sessionId.isEmpty()) {
            throw new IllegalArgumentException("Session ID is required for editing");
        }
        FilmSessionResponseDTO sessionToEdit = sessionService.getById(sessionId);
        if (sessionToEdit == null) {
            throw new NoDataFoundException("Session not found for ID: " + sessionId);
        }
        model.addAttribute("sessionToEdit", sessionToEdit);
    }

    private String handleAddAction(Map<String, String> params) {
        log.debug("Handling add action for new session...");
        Long movieId = ValidationUtil.parseLong(params.get("movieId"));

        ValidationUtil.validateDate(params.get("date"));
        ValidationUtil.validatePrice(params.get("price"));
        ValidationUtil.validateCapacity(params.get("capacity"));
        ValidationUtil.validateTime(params.get("startTime"), params.get("endTime"));

        FilmSessionCreateDTO createDTO = FilmSessionCreateDTO.builder()
                .price(new BigDecimal(params.get("price")))
                .date(LocalDate.parse(params.get("date")))
                .startTime(LocalTime.parse(params.get("startTime")))
                .endTime(LocalTime.parse(params.get("endTime")))
                .capacity(Integer.parseInt(params.get("capacity")))
                .build();

        return sessionService.save(createDTO, movieId);
    }

    private String handleEditSubmitAction(Map<String, String> params) {
        log.debug("Handling edit submit action...");
        Long movieId = ValidationUtil.parseLong(params.get("movieId"));

        FilmSessionUpdateDTO updateDTO = FilmSessionUpdateDTO.builder()
                .id(ValidationUtil.parseLong(params.get("id")))
                .price(new BigDecimal(params.get("price")))
                .date(LocalDate.parse(params.get("date")))
                .startTime(LocalTime.parse(params.get("startTime")))
                .endTime(LocalTime.parse(params.get("endTime")))
                .capacity(Integer.parseInt(params.get("capacity")))
                .build();

        return sessionService.update(updateDTO, movieId);
    }

    private String handleDeleteAction(String sessionId) {
        log.debug("Handling delete action for session ID: {}", sessionId);
        return sessionService.delete(sessionId);
    }

    private void handleError(RedirectAttributes redirectAttributes, String userMessage, String logMessage,
                             Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        redirectAttributes.addFlashAttribute("message", userMessage);
    }
}
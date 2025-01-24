package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.cinema.service.UserService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private static final String MESSAGE_PARAM = "message";

    private final TicketService ticketService;
    private final SessionService sessionService;
    private final UserService userService;

    @GetMapping
    public String showTicketsPage(@RequestParam(value = "action", required = false) String action,
                                  @RequestParam(value = "id", required = false) String ticketId,
                                  Model model) {
        log.debug("Handling GET request for tickets...");

        try {
            if ("edit".equals(action)) {
                handleEditAction(ticketId, model);
            }

            loadDataForView(model);
        } catch (IllegalArgumentException e) {
            handleError(model, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException e) {
            handleError(model, "Error! " + e.getMessage(), e);
        } catch (Exception e) {
            handleError(model, "An unexpected error occurred while fetching data", e);
        }
        return "tickets";
    }

    @PostMapping
    public String processTicketAction(@RequestParam String action, @RequestParam(required = false) String id,
                                      @RequestParam(required = false) String userId,
                                      @RequestParam(required = false) String sessionId,
                                      @RequestParam(required = false) String seatNumber,
                                      @RequestParam(required = false) String status,
                                      @RequestParam(required = false) String requestType,
                                      Model model) {
        log.debug("Handling POST request for tickets operations...");

        try {
            String message = processAction(action, id, userId, sessionId, seatNumber, status, requestType);
            model.addAttribute(MESSAGE_PARAM, message);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage(), e);
            model.addAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            log.warn("Business error: {}", e.getMessage(), e);
            model.addAttribute(MESSAGE_PARAM, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            model.addAttribute(MESSAGE_PARAM, "An unexpected error occurred");
        }
        return "redirect:/admin/tickets";
    }

    private String processAction(String action, String id, String userId, String sessionId, String seatNumber, String status, String requestType) {
        return switch (action) {
            case "add" -> handleAddAction(userId, sessionId, seatNumber, status, requestType);
            case "delete" -> handleDeleteAction(id);
            case "update" -> handleUpdateAction(id, userId, sessionId, seatNumber, status, requestType);
            default -> {
                log.warn("Unknown action requested: {}", action);
                yield "Unknown action requested";
            }
        };
    }

    private void loadDataForView(Model model) {
        log.debug("Loading data for view...");

        Set<UserResponseDTO> users = userService.findAll();
        model.addAttribute("users", users);

        Set<FilmSessionResponseDTO> filmSessions = sessionService.findAll();
        model.addAttribute("filmSessions", filmSessions);

        Set<TicketResponseDTO> tickets = ticketService.findAll();
        model.addAttribute("tickets", tickets);
    }

    private String handleAddAction(String userId, String sessionId, String seatNumber, String status, String requestType) {
        TicketCreateDTO createDTO = TicketCreateDTO.builder()
                .userId(ValidationUtil.parseLong(userId))
                .sessionId(ValidationUtil.parseLong(sessionId))
                .seatNumber(seatNumber)
                .status(status)
                .requestType(requestType)
                .build();
        return ticketService.save(createDTO);
    }

    private String handleDeleteAction(String id) {
        return ticketService.delete(id);
    }

    private String handleUpdateAction(String id, String userId, String sessionId, String seatNumber, String status, String requestType) {
        TicketUpdateDTO updateDTO = TicketUpdateDTO.builder()
                .id(ValidationUtil.parseLong(id))
                .userId(ValidationUtil.parseLong(userId))
                .sessionId(ValidationUtil.parseLong(sessionId))
                .seatNumber(seatNumber)
                .status(status)
                .requestType(requestType)
                .build();
        return ticketService.update(updateDTO);
    }

    private void handleEditAction(String ticketId, Model model) {
        TicketResponseDTO ticketToEdit = ticketService.getById(ticketId).orElseThrow(() ->
                new NoDataFoundException("Error! Ticket with ID " + ticketId + " doesn't exist!"));
        log.info(ticketToEdit.toString());
        model.addAttribute("ticketToEdit", ticketToEdit);
    }

    private void handleError(Model model, String message, Exception e) {
        log.error("{}: {}", message, e.getMessage(), e);
        model.addAttribute(MESSAGE_PARAM, message);
    }
}

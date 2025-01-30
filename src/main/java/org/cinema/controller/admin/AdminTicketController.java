package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.cinema.service.UserService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
public class AdminTicketController {

    private final TicketService ticketService;
    private final SessionService sessionService;
    private final UserService userService;

    @GetMapping
    public String showTicketsPage(@RequestParam(value = "action", required = false) String action,
                                  @RequestParam(value = "id", required = false) String ticketId,
                                  Model model, RedirectAttributes redirectAttributes) {
        log.debug("Handling GET request for tickets...");

        try {
            if ("edit".equals(action) && ticketId != null) {
                handleEditAction(ticketId, model);
            }
            loadDataForView(model);
        } catch (IllegalArgumentException e) {
            handleError(redirectAttributes, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException e) {
            handleError(redirectAttributes, "Error! " + e.getMessage(), e);
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while fetching data", e);
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
                                      RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for tickets operations...");

        try {
            String message = processAction(action, id, userId, sessionId, seatNumber, status, requestType);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (IllegalArgumentException e) {
            handleError(redirectAttributes, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            handleError(redirectAttributes, "Error! " + e.getMessage(), e);
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred", e);
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
        model.addAttribute("users", userService.findAll());
        model.addAttribute("filmSessions", sessionService.findAll());
        model.addAttribute("tickets", ticketService.findAll());
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

    private void handleError(RedirectAttributes redirectAttributes, String userMessage, Exception e) {
        log.error("{}: {}", userMessage, e.getMessage(), e);
        redirectAttributes.addFlashAttribute("message", userMessage);
    }
}

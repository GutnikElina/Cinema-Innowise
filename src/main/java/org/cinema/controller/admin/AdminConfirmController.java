package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.TicketService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tickets/confirm")
public class AdminConfirmController {

    private static final String MESSAGE_PARAM = "message";

    private final TicketService ticketService;

    @GetMapping
    public String showTicketsPage(Model model) {
        log.debug("Handling GET request for confirming tickets...");

        try {
            log.debug("Fetching tickets...");
            Set<TicketResponseDTO> tickets = ticketService.findAll();
            model.addAttribute("tickets", tickets);

        } catch (NoDataFoundException e) {
            handleError(model, "Error! " + e.getMessage(), "No tickets found: {}", e, e.getMessage());
        } catch (Exception e) {
            handleError(model, "An unexpected error occurred while fetching tickets",
                    "Unexpected error during tickets fetching: {}", e, e.getMessage());
        }

        return "confirmTickets";
    }

    @PostMapping
    public String processTicketConfirmation(@RequestParam(value = "action", required = false) String action,
                                            @RequestParam(value = "id", required = false) String ticketId,
                                           RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for confirming tickets...");

        try {
            ValidationUtil.validateParameters(action, ticketId);

            log.debug("Processing action {} for ticket ID {}", action, ticketId);
            String message = ticketService.processTicketAction(action, ValidationUtil.parseLong(ticketId));

            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
            return "redirect:/admin/tickets/confirm";

        } catch (IllegalArgumentException e) {
            log.warn("Validation error during ticket confirmation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
        } catch (NoDataFoundException e) {
            log.warn("Business error during ticket confirmation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during ticket confirmation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "An unexpected error occurred while processing the ticket");
        }

        return "redirect:/admin/tickets/confirm";
    }

    private void handleError(Model model, String userMessage, String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        model.addAttribute(MESSAGE_PARAM, userMessage);
        model.addAttribute("tickets", Collections.emptySet());
    }
}
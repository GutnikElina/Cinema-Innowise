package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tickets/confirm")
public class AdminConfirmController {

    private static final String MESSAGE_PARAM = "message";
    private static final String TICKETS_PARAM = "tickets";
    private static final String ACTION_PARAM = "action";
    private static final String ID_PARAM = "id";
    private static final String CONFIRM_TICKETS_PAGE = "confirmTickets";
    private static final String URL_REDIRECT = "redirect:/admin/tickets/confirm";

    private final TicketService ticketService;

    @GetMapping
    public String showTicketsPage(Model model) {
        log.debug("Handling GET request for confirming tickets page...");

        try {
            log.debug("Fetching tickets...");
            model.addAttribute(TICKETS_PARAM, ticketService.findAll());
        } catch (NoDataFoundException e) {
            handleError(model, "No tickets found: " + e.getMessage(), e);
        } catch (Exception e) {
            handleError(model, "An unexpected error occurred while fetching tickets", e);
        }
        return CONFIRM_TICKETS_PAGE;
    }

    @PostMapping
    public String processTicketConfirmation(@RequestParam(ACTION_PARAM) String action,
                                            @RequestParam(ID_PARAM) String ticketId,
                                            RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for confirming tickets page...");

        try {
            ValidationUtil.validateParameters(action, ticketId);

            log.debug("Processing action {} for ticket ID {}", action, ticketId);
            String message = ticketService.processTicketAction(action, ValidationUtil.parseLong(ticketId));

            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        } catch (IllegalArgumentException e) {
            handleRedirectError(redirectAttributes, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException e) {
            handleRedirectError(redirectAttributes, e.getMessage(), e);
        } catch (Exception e) {
            handleRedirectError(redirectAttributes, "An unexpected error occurred while processing the ticket", e);
        }
        return URL_REDIRECT;
    }

    private void handleError(Model model, String userMessage, Exception e) {
        log.error(userMessage, e);
        model.addAttribute(MESSAGE_PARAM, userMessage);
        model.addAttribute(TICKETS_PARAM, Collections.emptySet());
    }

    private void handleRedirectError(RedirectAttributes redirectAttributes, String userMessage, Exception e) {
        log.error(userMessage, e);
        redirectAttributes.addFlashAttribute(MESSAGE_PARAM, userMessage);
    }
}
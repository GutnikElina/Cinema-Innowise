package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.constants.PageConstant;
import org.cinema.constants.ParamConstant;
import org.cinema.constants.RedirectConstant;
import org.cinema.handler.ErrorHandler;
import org.cinema.service.TicketService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin/tickets/confirm")
@RequiredArgsConstructor
public class AdminConfirmController {

    private final TicketService ticketService;

    @GetMapping
    public String showTicketsPage(Model model) {
        log.debug("Fetching tickets...");
        try {
            model.addAttribute(ParamConstant.TICKETS_PARAM, ticketService.findAll());
        } catch (Exception e) {
            ErrorHandler.handleError(model, e);
        }
        return PageConstant.CONFIRM_TICKETS_PAGE;
    }

    @PostMapping("/{action}")
    public String handleTicketAction(@RequestParam(ParamConstant.ID_PARAM) String ticketId,
                                     @PathVariable(ParamConstant.ACTION_PARAM) String action,
                                     RedirectAttributes redirectAttributes) {
        return processTicketAction(ticketId, action, redirectAttributes);
    }

    private String processTicketAction(String ticketId, String action, RedirectAttributes redirectAttributes) {
        try {
            ValidationUtil.validateParameters(action, ticketId);
            log.debug("Processing action '{}' for ticket ID '{}'", action, ticketId);
            String message = ticketService.processTicketAction(action, ValidationUtil.parseLong(ticketId));
            redirectAttributes.addFlashAttribute(ParamConstant.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return RedirectConstant.REDIRECT_ADMIN_SESSIONS;
    }
}

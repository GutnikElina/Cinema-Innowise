package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.service.TicketService;
import org.cinema.handler.ErrorHandler;
import org.cinema.util.ConstantsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user/tickets")
@RequiredArgsConstructor
public class MyTicketsController {

    private final TicketService ticketService;

    @GetMapping
    public String getUserTickets(@SessionAttribute(ConstantsUtil.USER_ID_PARAM) Long userId, Model model) {
        log.debug("Handling GET request for getting user's tickets...");

        try {
            List<TicketResponseDTO> tickets = ticketService.findByUserId(userId.toString());
            model.addAttribute(ConstantsUtil.TICKETS_PARAM, tickets);
        } catch (Exception e) {
            ErrorHandler.handleError(model, e);
        }
        return ConstantsUtil.MY_TICKET_PAGE;
    }

    @PostMapping("/returnMyTickets")
    public String processTicketAction(@RequestParam(required = false) Long ticketId,
                                      RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for returning ticket...");

        try {
            if (ticketId != null) {
                String message = ticketService.processTicketAction(ConstantsUtil.RETURN_TICKETS_PARAM, ticketId);
                redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
            } else {
                redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM,
                        "Error! No ticket ID provided");
            }
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return ConstantsUtil.REDIRECT_USER_TICKETS;
    }
}
package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Set;

@Controller
@RequestMapping("/user/tickets")
@RequiredArgsConstructor
public class MyTicketsController {

    private static final String MESSAGE_PARAM = "message";

    private final TicketService ticketService;

    @GetMapping
    public String getUserTickets(@SessionAttribute("userId") Long userId, RedirectAttributes redirectAttributes) {
        try {
            Set<TicketResponseDTO> tickets = ticketService.findByUserId(userId.toString());
            redirectAttributes.addFlashAttribute("tickets", tickets);
        } catch (IllegalArgumentException | NoDataFoundException e) {
            handleError(redirectAttributes, "Error! " + e.getMessage());
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while loading tickets");
        }
        return "redirect:/user/tickets";
    }

    @PostMapping
    public String processTicketAction(@RequestParam String action,
                                      @RequestParam(required = false) Long ticketId,
                                      @SessionAttribute("userId") Long userId,
                                      RedirectAttributes redirectAttributes) {
        try {
            String message = processTicketAction(action, ticketId);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        } catch (IllegalArgumentException | NoDataFoundException e) {
            handleError(redirectAttributes, e.getMessage());
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while processing ticket action");
        }
        return "redirect:/user/tickets";
    }

    private String processTicketAction(String action, Long ticketId) {
        if ("returnMyTicket".equals(action)) {
            return ticketService.processTicketAction(action, ticketId);
        } else {
            return "Error! Unknown action requested";
        }
    }

    private void handleError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        redirectAttributes.addFlashAttribute("tickets", Collections.emptySet());
    }
}
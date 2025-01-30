package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String getUserTickets(@SessionAttribute("userId") Long userId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Set<TicketResponseDTO> tickets = ticketService.findByUserId(userId.toString());
            model.addAttribute("tickets", tickets);
        } catch (IllegalArgumentException | NoDataFoundException e) {
            handleError(redirectAttributes, "Error! " + e.getMessage());
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while loading tickets");
        }
        return "myTickets";
    }

    @PostMapping
    public String processTicketAction(@RequestParam String action,
                                      @RequestParam(required = false) Long ticketId,
                                      RedirectAttributes redirectAttributes) {
        try {
            if ("returnMyTicket".equals(action)) {
                String message = ticketService.processTicketAction(action, ticketId);
                redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
            } else {
                redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! Unknown action requested");
            }
        } catch (IllegalArgumentException | NoDataFoundException e) {
            handleError(redirectAttributes, e.getMessage());
        } catch (Exception e) {
            handleError(redirectAttributes, "An unexpected error occurred while processing ticket action");
        }
        return "redirect:/user/tickets";
    }

    private void handleError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        redirectAttributes.addFlashAttribute("tickets", Collections.emptySet());
    }
}
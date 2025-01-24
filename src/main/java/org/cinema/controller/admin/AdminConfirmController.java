package org.cinema.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.Ticket;
import org.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/admin/tickets")
public class AdminConfirmController {

    private final TicketService ticketService;

    @Autowired
    public AdminConfirmController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/confirm")
    public String getTickets(@RequestParam(value = "message", required = false) String message, Model model) {
        log.debug("Handling GET request for tickets...");

        try {
            Set<Ticket> tickets = ticketService.findAll();
            model.addAttribute("tickets", tickets);

            if (message != null && !message.isEmpty()) {
                model.addAttribute("message", message);
            }

        } catch (NoDataFoundException e) {
            log.error("No tickets found: {}", e.getMessage());
            model.addAttribute("tickets", Collections.emptySet());
            model.addAttribute("message", "Error! " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            model.addAttribute("tickets", Collections.emptySet());
            model.addAttribute("message", "An unexpected error occurred.");
        }
        return "confirmTickets";
    }

    @PostMapping("/confirm")
    public String processTicketAction(@RequestParam("action") String action,
                                      @RequestParam("id") String ticketId,
                                      Model model) {
        log.debug("Handling POST request for ticket action: {} with ID: {}", action, ticketId);

        try {
            String message = ticketService.processTicketAction(action, ticketId);
            return "redirect:/admin/tickets/confirm?message=" + message;
        } catch (IllegalArgumentException | NoDataFoundException e) {
            log.error("Error processing ticket action: {}", e.getMessage(), e);
            return "redirect:/admin/tickets/confirm?message=Error! " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error during ticket processing: {}", e.getMessage(), e);
            return "redirect:/admin/tickets/confirm?message=An unexpected error occurred.";
        }
    }
}


package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Set;

@Controller
@RequestMapping("/user/tickets/purchase")
@RequiredArgsConstructor
public class TicketPurchaseController {

    private static final String MESSAGE_PARAM = "message";

    private final TicketService ticketService;
    private final SessionService sessionService;

    @GetMapping
    public String showPurchasePage(@RequestParam(required = false) String date,
                                   @RequestParam(required = false) String sessionId,
                                   @RequestParam(required = false) String message,
                                   Model model) {
        try {
            Set<FilmSessionResponseDTO> filmSessions;

            if (date == null || date.isEmpty()) {
                filmSessions = sessionService.findAll();
            } else {
                filmSessions = sessionService.findByDate(date);

                if (filmSessions.isEmpty()) {
                    model.addAttribute(MESSAGE_PARAM, "No film sessions found for the selected date. Displaying all sessions.");
                    filmSessions = sessionService.findAll();
                }

                model.addAttribute("selectedDate", date);
            }

            model.addAttribute("filmSessions", filmSessions);

            if (sessionId != null && !sessionId.trim().isEmpty()) {
                FilmSessionResponseDTO selectedSession = ticketService.getSessionDetailsWithTickets(sessionId);
                model.addAttribute("selectedSession", selectedSession);
                model.addAttribute("sessionId", sessionId);
            }

            if (message != null && !message.isEmpty()) {
                model.addAttribute(MESSAGE_PARAM, message);
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
            model.addAttribute("filmSessions", Collections.emptySet());
            model.addAttribute("selectedSession", null);
        } catch (NoDataFoundException e) {
            model.addAttribute(MESSAGE_PARAM, "Error! " + e.getMessage());
            model.addAttribute("filmSessions", Collections.emptySet());
            model.addAttribute("selectedSession", null);
        } catch (Exception e) {
            model.addAttribute(MESSAGE_PARAM, "An unexpected error occurred while loading sessions.");
            model.addAttribute("filmSessions", Collections.emptySet());
            model.addAttribute("selectedSession", null);
        }

        return "purchase";
    }

    @PostMapping
    public String purchaseTicket(@RequestParam String sessionId,
                                 @RequestParam String seatNumber,
                                 @SessionAttribute("userId") Long userId,
                                 RedirectAttributes redirectAttributes) {
        try {
            TicketCreateDTO ticketCreateDTO = TicketCreateDTO.builder()
                    .userId(userId)
                    .sessionId(Long.valueOf(sessionId))
                    .seatNumber(seatNumber)
                    .build();

            String message = ticketService.purchaseTicket(ticketCreateDTO);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);

            return "redirect:/user/tickets/purchase";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "An unexpected error occurred while processing the purchase.");
        }

        return "redirect:/user/tickets/purchase";
    }
}
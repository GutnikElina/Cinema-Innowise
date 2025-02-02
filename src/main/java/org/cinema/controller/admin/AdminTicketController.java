package org.cinema.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.handler.ErrorHandler;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.cinema.service.UserService;
import org.cinema.util.ConstantsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin/tickets")
@RequiredArgsConstructor
@Validated
public class AdminTicketController {

    private final TicketService ticketService;
    private final SessionService sessionService;
    private final UserService userService;

    @GetMapping
    public String getTickets(Model model) {
        log.debug("Fetching all tickets...");
        model.addAttribute(ConstantsUtil.TICKETS_PARAM, ticketService.findAll());
        model.addAttribute(ConstantsUtil.USERS_PARAM, userService.findAll());
        model.addAttribute(ConstantsUtil.SESSIONS_PARAM, sessionService.findAll());
        return ConstantsUtil.TICKET_PAGE;
    }

    @GetMapping("/edit")
    public String getEditTicket(@RequestParam(ConstantsUtil.ID_PARAM) String ticketId, Model model) {
        try {
            TicketResponseDTO ticketToEdit = ticketService.getById(ticketId);
            model.addAttribute(ConstantsUtil.TICKET_TO_EDIT, ticketToEdit);
            model.addAttribute(ConstantsUtil.TICKETS_PARAM, ticketService.findAll());
            model.addAttribute(ConstantsUtil.USERS_PARAM, userService.findAll());
            model.addAttribute(ConstantsUtil.SESSIONS_PARAM, sessionService.findAll());
        } catch (NoDataFoundException e) {
            log.error("Ticket not found: {}", e.getMessage());
            model.addAttribute(ConstantsUtil.MESSAGE_PARAM, "Error! Ticket not found.");
        }
        return ConstantsUtil.TICKET_PAGE;
    }

    @PostMapping("/add")
    public String addTicket(@Valid @ModelAttribute TicketCreateDTO createDTO,
                            RedirectAttributes redirectAttributes) {
        try {
            String message = ticketService.save(createDTO);
            redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return ConstantsUtil.REDIRECT_ADMIN_TICKETS;
    }

    @PostMapping("/edit")
    public String editTicket(@Valid @ModelAttribute TicketUpdateDTO updateDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            String message = ticketService.update(updateDTO);
            redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);

        }
        return ConstantsUtil.REDIRECT_ADMIN_TICKETS;
    }

    @PostMapping("/delete")
    public String deleteTicket(@RequestParam(ConstantsUtil.ID_PARAM) String ticketId,
                               RedirectAttributes redirectAttributes) {
        try {
            String message = ticketService.delete(ticketId);
            redirectAttributes.addFlashAttribute(ConstantsUtil.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return ConstantsUtil.REDIRECT_ADMIN_TICKETS;
    }
}

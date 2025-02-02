package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.handler.ErrorHandler;
import org.cinema.mapper.ticketMapper.TicketCreateMapper;
import org.cinema.constants.PageConstant;
import org.cinema.constants.ParamConstant;
import org.cinema.constants.RedirectConstant;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user/tickets/purchase")
@RequiredArgsConstructor
public class TicketPurchaseController {

    private final TicketService ticketService;
    private final SessionService sessionService;

    @GetMapping
    public String showPurchasePage(@RequestParam(required = false) String date,
                                   @RequestParam(required = false) String sessionId,
                                   @RequestParam(required = false) String message,
                                   Model model) {
        log.debug("Handling GET request for purchase page...");

        try {
            List<FilmSessionResponseDTO> filmSessions = getFilmSessions(date, model);
            model.addAttribute(ParamConstant.SESSIONS_PARAM, filmSessions);

            if (StringUtils.isNotBlank(sessionId)) {
                addSelectedSessionToModel(sessionId, model);
            }

            if (StringUtils.isNotBlank(message)) {
                model.addAttribute(ParamConstant.MESSAGE_PARAM, message);
            }
        } catch (Exception e) {
            ErrorHandler.handleError(model, e);
        }
        return PageConstant.PURCHASE_PAGE;
    }

    @PostMapping
    public String purchaseTicket(@RequestParam String sessionId,
                                 @RequestParam String seatNumber,
                                 @SessionAttribute("userId") Long userId,
                                 RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for purchase page...");

        try {
            TicketCreateDTO ticketCreateDTO = TicketCreateMapper.INSTANCE.toDTO(userId,
                    Long.valueOf(sessionId), seatNumber);
            String message = ticketService.purchaseTicket(ticketCreateDTO);
            redirectAttributes.addFlashAttribute(ParamConstant.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return RedirectConstant.REDIRECT_USER_PURCHASE;
    }

    private List<FilmSessionResponseDTO> getFilmSessions(String date, Model model) {
        List<FilmSessionResponseDTO> filmSessions;
        if (StringUtils.isBlank(date)) {
            filmSessions = sessionService.findAll();
        } else {
            filmSessions = sessionService.findByDate(date);

            if (filmSessions.isEmpty()) {
                model.addAttribute(ParamConstant.MESSAGE_PARAM,
                        "No film sessions found for the selected date. Displaying all sessions.");
                filmSessions = sessionService.findAll();
            }
            model.addAttribute(ParamConstant.SELECTED_DATE_PARAM, date);
        }
        return filmSessions;
    }

    private void addSelectedSessionToModel(String sessionId, Model model) {
        try {
            FilmSessionResponseDTO selectedSession = ticketService.getSessionDetailsWithTickets(sessionId);
            model.addAttribute(ParamConstant.SELECTED_SESSION_PARAM, selectedSession);
            model.addAttribute(ParamConstant.SESSION_ID_PARAM, sessionId);
        } catch (Exception e) {
            model.addAttribute(ParamConstant.MESSAGE_PARAM, "Failed to load session details.");
            log.error("Error loading session details for sessionId {}", sessionId, e);
        }
    }
}

package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.filmSessionDTO.FilmSessionResponseDTO;
import org.cinema.dto.ticketDTO.TicketCreateDTO;
import org.cinema.dto.ticketDTO.TicketResponseDTO;
import org.cinema.dto.ticketDTO.TicketUpdateDTO;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.SessionService;
import org.cinema.service.TicketService;
import org.cinema.service.UserService;
import org.cinema.service.impl.SessionServiceImpl;
import org.cinema.service.impl.TicketServiceImpl;
import org.cinema.service.impl.UserServiceImpl;
import org.cinema.util.ValidationUtil;
import java.io.IOException;
import java.util.Set;

@Slf4j
@WebServlet(name = "AdminTicketServlet", urlPatterns = {"/admin/tickets"})
public class AdminTicketServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/tickets.jsp";
    private static final String REDIRECT_PATH = "/admin/tickets";
    private static final String MESSAGE_PARAM = "message";

    private TicketService ticketService;
    private SessionService sessionService;
    private UserService userService;

    @Override
    public void init() {
        ticketService = TicketServiceImpl.getInstance();
        userService = UserServiceImpl.getInstance();
        sessionService = SessionServiceImpl.getInstance();
        log.info("AdminTicketServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for tickets...");

        try {
            String action = request.getParameter("action");
            if ("edit".equals(action)) {
                handleEditAction(request);
            }

            loadDataForView(request);
        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(), e);
        } catch (NoDataFoundException e) {
            handleError(request, "Error! " + e.getMessage(), e);
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while fetching data", e);
        }
        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for tickets operations...");

        try {
            String action = request.getParameter("action");
            String message = processAction(action, request);
            request.getSession().setAttribute(MESSAGE_PARAM, message);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage(), e);
            request.getSession().setAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            log.warn("Business error: {}", e.getMessage(), e);
            request.getSession().setAttribute(MESSAGE_PARAM, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            request.getSession().setAttribute(MESSAGE_PARAM, "An unexpected error occurred");
        }
        response.sendRedirect(request.getContextPath() + REDIRECT_PATH);
    }

    private String processAction(String action, HttpServletRequest request) {
        return switch (action) {
            case "add" -> handleAddAction(request);
            case "delete" -> handleDeleteAction(request);
            case "update" -> handleUpdateAction(request);
            default -> {
                log.warn("Unknown action requested: {}", action);
                yield "Unknown action requested";
            }
        };
    }

    private void loadDataForView(HttpServletRequest request) {
        log.debug("Loading data for view...");

        Set<UserResponseDTO> users = userService.findAll();
        request.setAttribute("users", users);

        Set<FilmSessionResponseDTO> filmSessions = sessionService.findAll();
        request.setAttribute("filmSessions", filmSessions);

        Set<TicketResponseDTO> tickets = ticketService.findAll();
        request.setAttribute("tickets", tickets);
    }

    private String handleAddAction(HttpServletRequest request) {
        TicketCreateDTO createDTO = TicketCreateDTO.builder()
                .userId(ValidationUtil.parseLong(getRequiredParameter(request, "userId")))
                .sessionId(ValidationUtil.parseLong(getRequiredParameter(request, "sessionId")))
                .seatNumber(getRequiredParameter(request, "seatNumber"))
                .status(getRequiredParameter(request, "status"))
                .requestType(getRequiredParameter(request, "requestType"))
                .build();
        return ticketService.save(createDTO);
    }

    private String handleDeleteAction(HttpServletRequest request) {
        return ticketService.delete(getRequiredParameter(request, "id"));
    }

    private String handleUpdateAction(HttpServletRequest request) {
        TicketUpdateDTO updateDTO = TicketUpdateDTO.builder()
                .id(ValidationUtil.parseLong(getRequiredParameter(request, "id")))
                .userId(ValidationUtil.parseLong(getRequiredParameter(request, "userId")))
                .sessionId(ValidationUtil.parseLong(getRequiredParameter(request, "sessionId")))
                .seatNumber(getRequiredParameter(request, "seatNumber"))
                .status(getRequiredParameter(request, "status"))
                .requestType(getRequiredParameter(request, "requestType"))
                .build();
        return ticketService.update(updateDTO);
    }

    private void handleEditAction(HttpServletRequest request) {
        String ticketId = getRequiredParameter(request, "id");
        TicketResponseDTO ticketToEdit = ticketService.getById(ticketId).orElseThrow(() ->
                new NoDataFoundException("Error!Ticket with ID " + ticketId + " doesn't exist!"));
        log.info(ticketToEdit.toString());
        request.setAttribute("ticketToEdit", ticketToEdit);
    }

    private String getRequiredParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value.trim();
    }

    private void handleError(HttpServletRequest request, String message, Exception e) {
        log.error("{}: {}", message, e.getMessage(), e);
        request.setAttribute(MESSAGE_PARAM, message);
    }
}
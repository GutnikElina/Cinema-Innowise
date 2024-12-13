package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.model.User;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/users.jsp";
    private static final String REDIRECT_PATH = "/admin/users";
    private static final String MESSAGE_PARAM = "message";

    private UserService userService;

    @Override
    public void init() {
        userService = UserServiceImpl.getInstance();
        log.info("AdminUserServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for users...");

        try {
            if ("edit".equals(request.getParameter("action"))) {
                handleEditAction(request);
            }
            
            loadDataForView(request);
            
            String message = request.getParameter(MESSAGE_PARAM);
            if (message != null && !message.isEmpty()) {
                request.setAttribute(MESSAGE_PARAM, message);
            }

        } catch (IllegalArgumentException e) {
            handleError(request, "Invalid input: " + e.getMessage(), e);
            setEmptyCollections(request);
        } catch (NoDataFoundException e) {
            handleError(request, e.getMessage(), e);
            setEmptyCollections(request);
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while fetching users", e);
            setEmptyCollections(request);
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for users operations...");

        try {
            String action = request.getParameter("action");
            String message = processAction(action, request);
            request.getSession().setAttribute(MESSAGE_PARAM, message);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage(), e);
            request.getSession().setAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            log.warn("Business error: {}", "Error! " + e.getMessage(), e);
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
        Set<User> users = userService.findAll();
        request.setAttribute("users", users);
    }

    private void setEmptyCollections(HttpServletRequest request) {
        request.setAttribute("users", Collections.emptySet());
    }

    private String handleAddAction(HttpServletRequest request) {
        return userService.save(
                getRequiredParameter(request, "username"),
                getRequiredParameter(request, "password"),
                getRequiredParameter(request, "role")
        );
    }

    private String handleUpdateAction(HttpServletRequest request) {
        return userService.update(
                getRequiredParameter(request, "id"),
                getRequiredParameter(request, "username"),
                getRequiredParameter(request, "password"),
                getRequiredParameter(request, "role")
        );
    }

    private String handleDeleteAction(HttpServletRequest request) {
        return userService.delete(getRequiredParameter(request, "id"));
    }

    private void handleEditAction(HttpServletRequest request) {
        String userId = getRequiredParameter(request, "id");
        User user = userService.getById(userId)
                .orElseThrow(() -> new NoDataFoundException("Error! User with ID " + userId + " doesn't exist."));
        request.setAttribute("user", user);
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

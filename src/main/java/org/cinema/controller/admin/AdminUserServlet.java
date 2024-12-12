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

    private UserService userService;

    @Override
    public void init() {
        userService = UserServiceImpl.getInstance();
        log.info("AdminUserServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for get users...");

        Set<User> users = Collections.emptySet();
        String message = request.getParameter("message");

        try {
            if ("edit".equals(request.getParameter("action"))) {
                handleEditAction(request);
            }
            users = userService.findAll();
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
            log.error("Validation error! {}", message, e);
        } catch (NoDataFoundException e) {
            message = "Error! " + e.getMessage();
            log.error("Error while doing users operation: {}", e.getMessage(), e);
        } catch (Exception e) {
            message = "Unexpected error occurred during fetching users";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        request.setAttribute("users", users);
        if (message != null && !message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for users operations...");

        String message = "";
        String action = request.getParameter("action");
        try {
            message = switch (action) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", request.getParameter("action"));
                    yield "Error! Unknown action.";
                }
            };
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during user operation: {}", message, e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            message = e.getMessage();
            log.error("Error while doing users operation: {}", message, e);
        } catch (Exception e) {
            message = "Unexpected error occurred during handling users operation '" + action + "'";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        // Redirect to GET with message as parameter
        String encodedMessage = response.encodeRedirectURL(message);
        response.sendRedirect(request.getContextPath() + "/admin/users?message=" + encodedMessage);
    }

    private String handleAddAction(HttpServletRequest request) {
        return userService.save(request.getParameter("username"),
                request.getParameter("password"), request.getParameter("role"));
    }

    private String handleUpdateAction(HttpServletRequest request) {
        return userService.update(request.getParameter("id"), request.getParameter("username"),
                request.getParameter("password"), request.getParameter("role"));
    }

    private String handleDeleteAction(HttpServletRequest request) {
        return userService.delete(request.getParameter("id"));
    }

    private void handleEditAction(HttpServletRequest request) {
        User user = userService.getById(request.getParameter("id"))
                .orElseThrow(() -> new NoDataFoundException("User with this ID doesn't exist."));
        request.setAttribute("user", user);
    }
}

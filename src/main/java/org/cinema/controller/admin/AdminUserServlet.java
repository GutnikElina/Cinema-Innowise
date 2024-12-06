package org.cinema.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.User;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> users = Collections.emptyList();
        String message = "";

        try {
            if ("edit".equals(request.getParameter("action"))) {
                handleEditAction(request);
            }
            users = userService.findAll();
        } catch (Exception e) {
            log.error("Unexpected error in doGet method (catch AdminUserServlet): {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }

        request.setAttribute("users", users);
        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String message = "";
        try {
            message = switch (request.getParameter("action")) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", request.getParameter("action"));
                    yield "Error! Unknown action.";
                }
            };
        } catch (Exception e) {
            log.error("Unexpected error in doPost method (catch AdminUserServlet): {}", e.getMessage(), e);
            message = "An unknown error occurred.";
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }

        doGet(request, response);
    }

    private String handleAddAction(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        return userService.save(username, password, role);
    }

    private String handleUpdateAction(HttpServletRequest request) {
        int userId = Integer.parseInt(request.getParameter("id"));
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        return userService.update(userId, username, password, role);
    }

    private String handleDeleteAction(HttpServletRequest request) {
        int userId = Integer.parseInt(request.getParameter("id"));
        return userService.delete(userId);
    }

    private void handleEditAction(HttpServletRequest request) {
        int userId = Integer.parseInt(request.getParameter("id"));
        User user = userService.getById(userId).orElse(null);
        request.setAttribute("user", user);
    }
}

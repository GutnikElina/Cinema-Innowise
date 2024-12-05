package org.cinema.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dao.UserDAO;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.util.PasswordUtil;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.cinema.util.ValidationUtil.*;

@Slf4j
@WebServlet("/admin/users")
public class AdminUserServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> users = Collections.emptyList();
        String action = request.getParameter("action");
        String message = "";

        try {
            if ("edit".equals(action)) {
                handleEditAction(request);
            }
            users = userDAO.getAll();
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
        String action = request.getParameter("action");
        String message = "";

        try {
            message = switch (action) {
                case "add" -> handleAddAction(request);
                case "delete" -> handleDeleteAction(request);
                case "update" -> handleUpdateAction(request);
                default -> {
                    log.warn("Unknown action: {}", action);
                    yield "Error! Unknown action.";
                }
            };
        } catch (HibernateException e) {
            log.error("Hibernate error (catch AdminUserServlet): ", e);
            message = "Occurred error while performing database operation.";
        } catch (RuntimeException e) {
            log.error("Unexpected RuntimeException error (catch AdminUserServlet): {}", e.getMessage(), e);
            message = "Unexpected error occurred, please try again later.";
        } catch (Exception e) {
            log.error("Unknown error (catch AdminUserServlet): {}", e.getMessage(), e);
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

        try {
            validateUsername(username);
            validatePassword(password);
            validateRole(role);

            Role userRole = Role.valueOf(role.toUpperCase());

            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(password, salt);

            User user = new User(username, hashedPassword, salt, userRole);
            userDAO.add(user);
            return "Success! User was successfully added!";
        } catch (IllegalArgumentException e) {
            log.error("Occurred error while adding user: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        }
    }

    private String handleUpdateAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String role = request.getParameter("role");

            validateUsername(username);
            validatePassword(password);
            validateRole(role);

            Role userRole = Role.valueOf(role.toUpperCase());
            User existingUser = userDAO.getById(id).orElseThrow(() -> new IllegalArgumentException("User with this ID doesn't exist!"));
            existingUser.setUsername(username);

            String salt = PasswordUtil.generateSalt();
            String hashedPassword = PasswordUtil.hashPassword(password, salt);

            existingUser.setPassword(hashedPassword);
            existingUser.setSalt(salt);
            existingUser.setRole(userRole);

            userDAO.update(existingUser);
            return "Success! User was successfully updated!";
        } catch (IllegalArgumentException e) {
            log.error("Error updating user, illegal argument (catch AdminUserServlet): {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        }
    }

    private String handleDeleteAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            userDAO.delete(id);
            return "Success! User was successfully deleted!";
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format during delete: {}", e.getMessage(), e);
            return "Error! Invalid user ID format.";
        }
    }

    private void handleEditAction(HttpServletRequest request) {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            User user = userDAO.getById(userId).orElse(null);
            request.setAttribute("user", user);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", e.getMessage(), e);
            request.setAttribute("message", "Error! Invalid user ID format.");
        }
    }
}
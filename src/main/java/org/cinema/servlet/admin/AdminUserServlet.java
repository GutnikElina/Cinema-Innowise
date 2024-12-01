package org.cinema.servlet.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dao.UserDAO;
import org.cinema.model.FilmSession;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.util.PasswordUtil;
import org.hibernate.HibernateException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
                    yield "Unknown action.";
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
        String role = request.getParameter("role").toUpperCase();

        try {
            Role userRole = Role.valueOf(role);
            User user = new User(username, PasswordUtil.hashPassword(password), userRole);
            userDAO.add(user);
            return "User was successfully added!";
        } catch (IllegalArgumentException e) {
            log.error("Occurred error while adding user: {}", e.getMessage(), e);
            return e.getMessage();
        }
    }

    private String handleDeleteAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            userDAO.delete(id);
            return "User was successfully deleted!";
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format during delete: {}", e.getMessage(), e);
            return "Invalid user ID format.";
        }
    }

    private String handleUpdateAction(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String role = request.getParameter("role").toUpperCase();

            Role userRole = Role.valueOf(role);
            User existingUser = userDAO.getById(id).orElseThrow(() -> new IllegalArgumentException("User with this ID doesn't exist!"));
            existingUser.setUsername(username);
            existingUser.setPassword(PasswordUtil.hashPassword(password));
            existingUser.setRole(userRole);

            userDAO.update(existingUser);
            return "User was successfully updated!";
        } catch (IllegalArgumentException e) {
            log.error("Error updating user, illegal argument (catch AdminUserServlet): {}", e.getMessage(), e);
            return e.getMessage();
            }
    }

    private void handleEditAction(HttpServletRequest request) {
        try {
            int userId = Integer.parseInt(request.getParameter("id"));
            User user = userDAO.getById(userId).orElse(null);
            request.setAttribute("user", user);
        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", e.getMessage(), e);
            request.setAttribute("message", "Invalid user ID format.");
            //request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
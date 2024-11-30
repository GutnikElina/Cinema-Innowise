package org.cinema.servlet;

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
import java.io.IOException;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("edit".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("id"));
            User user = userDAO.getById(userId).orElse(null);
            request.setAttribute("user", user);
        }

        List<User> users = userDAO.getAll();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/WEB-INF/views/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String message = "";

        try {
            if ("add".equals(action)) {
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String role = request.getParameter("role").toUpperCase();

                if (Role.valueOf(role) == null) {
                    throw new IllegalArgumentException("Указана неверная роль.");
                }

                User user = new User(username, PasswordUtil.hashPassword(password), Role.valueOf(role));
                userDAO.add(user);
                message = "Пользователь успешно добавлен!";
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                userDAO.delete(id);
                message = "Пользователь успешно удален!";
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String role = request.getParameter("role").toUpperCase();

                if (Role.valueOf(role) == null) {
                    throw new IllegalArgumentException("Указана неверная роль.");
                }

                User existingUser = userDAO.getById(id).orElseThrow(() -> new IllegalArgumentException("Пользователь с таким ID не найден."));
                existingUser.setUsername(username);
                existingUser.setPassword(PasswordUtil.hashPassword(password));
                existingUser.setRole(Role.valueOf(role));

                userDAO.update(existingUser);
                message = "Пользователь успешно обновлен!";
            }
        } catch (IllegalArgumentException e) {
            message = e.getMessage();
        } catch (Exception e) {
            message = "Ошибка: " + e.getMessage();
        }

        request.setAttribute("message", message);
        doGet(request, response);
    }
}
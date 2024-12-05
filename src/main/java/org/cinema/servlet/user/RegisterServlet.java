package org.cinema.servlet.user;

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
import org.cinema.util.ValidationUtil;
import java.io.IOException;

@WebServlet("/registration")
@Slf4j
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        log.info("RegisterServlet initialized with UserDAO.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.info("Handling GET request for registration...");
        request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String message = "";

        try {

            String username = request.getParameter("newLogin");
            String password = request.getParameter("newPassword");

            ValidationUtil.validateUsername(username);
            ValidationUtil.validatePassword(password);

            User user = new User(username, PasswordUtil.hashPassword(password), Role.USER);
            userDAO.add(user);

            log.info("User [{}] registered successfully.", username);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);

        } catch (Exception e) {
            message = e.getMessage();
            log.error("Error during registration: {}", e.getMessage());
        }

        if (!message.isEmpty()) {
            request.setAttribute("message", message);
        }
        request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
    }
}

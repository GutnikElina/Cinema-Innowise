package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.cinema.repository.UserRepository;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.util.PasswordUtil;
import java.io.IOException;

@WebServlet("/login")
@Slf4j
public class LoginServlet extends HttpServlet {

    private UserRepository userRepository;

    @Override
    public void init() {
        userRepository = new UserRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("login");
        String password = request.getParameter("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("message", "Username and password cannot be empty.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userRepository.getByUsername(username).orElse(null);

            if (user == null || !PasswordUtil.checkPassword(password, user.getPassword())) {
                request.setAttribute("message", "Invalid username or password.");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().toString());

            if (user.getRole() == Role.ADMIN) {
                log.info("Admin [{}] logged in successfully.", username);
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                log.info("User [{}] logged in successfully.", username);
                response.sendRedirect(request.getContextPath() + "/user");
            }
        } catch (Exception e) {
            log.error("Error during authorization: {}", e.getMessage());
            request.setAttribute("message", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}

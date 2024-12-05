package org.cinema.servlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dao.UserDAO;
import org.cinema.model.User;
import org.cinema.util.PasswordUtil;
import java.io.IOException;
import java.util.Objects;

@WebServlet("/login")
@Slf4j
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
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
            request.setAttribute("errorMessage", "Username and password cannot be empty.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.getByUsername(username).orElse(null);

        if (user == null || !PasswordUtil.hashPassword(password, user.getSalt()).equals(user.getPassword())) {
            request.setAttribute("message", "Invalid username or password.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole().toString());

        log.info("User [{}] logged in successfully.", username);
        response.sendRedirect(request.getContextPath() + "/user");
    }
}

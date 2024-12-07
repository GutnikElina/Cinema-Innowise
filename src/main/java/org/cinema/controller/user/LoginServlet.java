package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;

import java.io.IOException;

@WebServlet("/login")
@Slf4j
public class LoginServlet extends HttpServlet {

    private UserService loginService;

    @Override
    public void init() {
        this.loginService = UserServiceImpl.getInstance();
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

        try {
            HttpSession session = loginService.auth(username, password, request.getSession());
            String role = (String) session.getAttribute("role");

            if ("ADMIN".equals(role)) {
                log.info("Admin [{}] logged in successfully.", username);
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                log.info("User [{}] logged in successfully.", username);
                response.sendRedirect(request.getContextPath() + "/user");
            }
        } catch (IllegalArgumentException e) {
            log.error("Authentication error: {}", e.getMessage());
            request.setAttribute("message", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}

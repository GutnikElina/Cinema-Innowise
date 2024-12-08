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

@Slf4j
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private UserService loginService;

    @Override
    public void init() {
        this.loginService = UserServiceImpl.getInstance();
        log.info("LoginServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for authorization...");
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for authorization...");

        String username = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            HttpSession session = loginService.login(username, password, request.getSession());
            String role = (String) session.getAttribute("role");

            if ("ADMIN".equals(role)) {
                log.info("Admin '{}' logged in successfully.", username);
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                log.info("User '{}' logged in successfully.", username);
                response.sendRedirect(request.getContextPath() + "/user");
            }
        } catch (IllegalArgumentException e) {
            log.warn("Login failed for user '{}': {}", username, e.getMessage(), e);
            request.setAttribute("message", "Error! "+ e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login for user '{}': {}", username, e.getMessage(), e);
            request.setAttribute("message", "An unexpected error occurred. Please try again later.");
        }
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }
}

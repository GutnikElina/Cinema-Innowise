package org.cinema.controller.general;

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

    private static final String VIEW_PATH = "/WEB-INF/views/login.jsp";
    private static final String ADMIN_REDIRECT_PATH = "/admin";
    private static final String USER_REDIRECT_PATH = "/user";
    private static final String MESSAGE_PARAM = "message";

    private UserService loginService;

    @Override
    public void init() {
        this.loginService = UserServiceImpl.getInstance();
        log.info("LoginServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for login page...");
        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for authorization...");

        try {
            String username = getRequiredParameter(request, "login");
            String password = getRequiredParameter(request, "password");

            processLogin(request, response, username, password);

        } catch (IllegalArgumentException e) {
            log.warn("Login validation error: {}", e.getMessage());
            handleError(request, "Error! " + e.getMessage());
            forwardToLoginPage(request, response);
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage(), e);
            handleError(request, "Error! An unexpected error occurred. Please try again later.");
            forwardToLoginPage(request, response);
        }
    }

    private void processLogin(HttpServletRequest request, HttpServletResponse response,
            String username, String password) throws IOException, ServletException {
        try {
            HttpSession session = loginService.login(username, password, request.getSession());
            String role = (String) session.getAttribute("role");

            if ("ADMIN".equals(role)) {
                log.info("Admin '{}' logged in successfully", username);
                redirectToPath(request, response, ADMIN_REDIRECT_PATH);
            } else {
                log.info("User '{}' logged in successfully", username);
                redirectToPath(request, response, USER_REDIRECT_PATH);
            }

        } catch (IllegalArgumentException e) {
            log.warn("Login failed for user '{}': {}", username, e.getMessage());
            handleError(request, "Error! " + e.getMessage());
            forwardToLoginPage(request, response);
        }
    }

    private String getRequiredParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return value.trim();
    }

    private void handleError(HttpServletRequest request, String message) {
        request.setAttribute(MESSAGE_PARAM, message);
    }

    private void forwardToLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    private void redirectToPath(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }
}

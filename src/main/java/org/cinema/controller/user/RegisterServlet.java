package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;

import java.io.IOException;

@Slf4j
@WebServlet(name = "RegisterServlet", urlPatterns = {"/registration"})
public class RegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        this.userService = UserServiceImpl.getInstance();
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

            userService.register(username, password);
            log.info("User [{}] registered successfully.", username);

            request.setAttribute("successMessage", "Registration successful! Please log in.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        } catch (Exception e) {
            message = e.getMessage();
            log.error("Error during registration: {}", e.getMessage());
        }

        request.setAttribute("message", message);
        request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
    }
}

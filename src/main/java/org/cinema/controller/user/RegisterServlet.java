package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.exception.EntityAlreadyExistException;
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
        log.info("RegisterServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for registration...");
        request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for registration...");
        String username = request.getParameter("newLogin");
        String password = request.getParameter("newPassword");

        String message = "";
        try {
            userService.register(username, password);
            log.info("User '{}' registered successfully.", username);
            request.setAttribute("message", "Success! Registration successful! Please log in.");
            return;
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during registration: {}", message, e);
        } catch (EntityAlreadyExistException e) {
            message = "Error! "+ e.getMessage();
            log.error("Error during registration: {}", e.getMessage(), e);
        } catch (Exception e) {
            message = "Error! "+ e.getMessage();
            log.error("Unexpected error during registration: {}", e.getMessage(), e);
        }

        request.setAttribute("message", message);
        request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
    }
}

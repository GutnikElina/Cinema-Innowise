package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.userDTO.UserCreateDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;
import java.io.IOException;

@Slf4j
@WebServlet(name = "RegisterServlet", urlPatterns = {"/registration"})
public class RegisterServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/registration.jsp";
    private static final String LOGIN_PATH = "/login";
    private static final String MESSAGE_PARAM = "message";

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

        String message = request.getParameter(MESSAGE_PARAM);
        if (message != null && !message.isEmpty()) {
            request.setAttribute(MESSAGE_PARAM, message);
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for registration...");

        try {
            UserUpdateDTO userCreateDTO = UserUpdateDTO.builder()
                    .username(request.getParameter("newLogin"))
                    .password(request.getParameter("newPassword"))
                    .build();
            userService.register(userCreateDTO);
            handleSuccessfulRegistration(request, response);
            return;
        } catch (IllegalArgumentException e) {
            handleRegistrationError(request, "Invalid input: " + e.getMessage(),
                    "Validation error during registration attempt", e);
        } catch (EntityAlreadyExistException e) {
            handleRegistrationError(request, "User with this login already exists",
                    "Registration failed - user already exists: {}", e, e.getMessage());
        } catch (Exception e) {
            handleRegistrationError(request, "An unexpected error occurred during registration",
                    "Unexpected error during registration attempt: {}", e, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/registration");
    }

    private void handleSuccessfulRegistration(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        log.info("User successfully registered!");

        response.sendRedirect(request.getContextPath() + LOGIN_PATH + "?" + MESSAGE_PARAM + "=" +
                response.encodeRedirectURL("Registration successful! Please login."));
    }

    private void handleRegistrationError(HttpServletRequest request, String userMessage,
                                         String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        request.getSession().setAttribute(MESSAGE_PARAM, userMessage);
    }
}

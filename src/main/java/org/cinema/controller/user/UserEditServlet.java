package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@WebServlet(name = "UserEditServlet", urlPatterns = {"/user/edit"})
public class UserEditServlet extends HttpServlet {

    private static final String VIEW_PATH = "/WEB-INF/views/editProfile.jsp";
    private static final String MESSAGE_PARAM = "message";

    private UserService userService;

    @Override
    public void init() {
        userService = UserServiceImpl.getInstance();
        log.info("UserEditServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for profile editing...");

        try {
            Long userId = getUserId(request.getSession());
            log.debug("Loading profile for user ID: {}", userId);

            UserResponseDTO user = userService.getById(String.valueOf(userId))
                    .orElseThrow(() -> new NoDataFoundException("User not found with ID: " + userId));
            request.setAttribute("user", user);

            String message = request.getParameter(MESSAGE_PARAM);
            if (message != null && !message.isEmpty()) {
                request.setAttribute(MESSAGE_PARAM, message);
            }

        } catch (IllegalArgumentException e) {
            handleError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error during profile loading", e);
        } catch (NoDataFoundException e) {
            handleError(request, "Error! " + e.getMessage(),
                    "No user found: {}", e, e.getMessage());
        } catch (Exception e) {
            handleError(request, "An unexpected error occurred while loading profile",
                    "Unexpected error during profile loading: {}", e, e.getMessage());
        }

        request.getRequestDispatcher(VIEW_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for profile editing...");

        try {
            String password = !Objects.equals(request.getParameter("password"), "") ?
                    request.getParameter("password") : "null";
            Long userId = getUserId(request.getSession());
            UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                    .username(request.getParameter("username"))
                    .password(password)
                    .build();

            log.debug("Updating profile for user ID: {}", userId);
            userService.updateProfile(userId, userUpdateDTO);

            response.sendRedirect(request.getContextPath() + "/user/edit?" + MESSAGE_PARAM + "=" +
                    response.encodeRedirectURL("Success! Profile updated successfully."));
            return;

        } catch (IllegalArgumentException e) {
            handleSessionError(request, "Error! Invalid input: " + e.getMessage(),
                    "Validation error during profile update", e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            handleSessionError(request, "Error! " + e.getMessage(),
                    "Business error during profile update: {}", e, e.getMessage());
        } catch (Exception e) {
            handleSessionError(request, "An unexpected error occurred while updating profile",
                    "Unexpected error during profile update: {}", e, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/user/edit");
    }

    private Long getUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in session");
        }
        return userId;
    }

    private void handleError(HttpServletRequest request, String userMessage,
            String logMessage, Exception e, Object... logParams) {
        if (e != null) {
            log.error(logMessage, logParams, e);
        } else {
            log.warn(logMessage, logParams);
        }
        request.setAttribute(MESSAGE_PARAM, userMessage);
        request.setAttribute("user", null);
    }

    private void handleSessionError(HttpServletRequest request, String userMessage,
            String logMessage, Exception e, Object... logParams) {
        log.error(logMessage, logParams, e);
        request.getSession().setAttribute(MESSAGE_PARAM, userMessage);
    }
}
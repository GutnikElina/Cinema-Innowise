package org.cinema.controller.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.EntityAlreadyExistException;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.User;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;
import java.io.IOException;

@Slf4j
@WebServlet(name = "UserEditServlet", urlPatterns = {"/user/edit"})
public class UserEditServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() {
        this.userService = UserServiceImpl.getInstance();
        log.info("EditProfileServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling GET request for profile editing...");

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            handleUnauthorizedAccess(request, response);
            return;
        }

        User user = userService.getById(String.valueOf(userId))
                .orElseThrow(() -> new NoDataFoundException("User not found with ID: " + userId));

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/views/editProfile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Handling POST request for profile editing...");

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            handleUnauthorizedAccess(request, response);
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String message = "";

        try {
            if (password != null && password.trim().isEmpty()) {
                password = null;
            }
            userService.updateProfile(userId, username, password);
            message = "Success! Profile updated successfully.";
            log.info("User with ID {} updated their profile.", userId);
        } catch (IllegalArgumentException e) {
            message = "Validation error! " + e.getMessage();
            log.error("Validation error during updating user profile: {}", message, e);
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            message = e.getMessage();
            log.error("Error during updating user profile: {}", message, e);
        } catch (Exception e) {
            message = "Unexpected error while updating user profile";
            log.error("{}: {}", message, e.getMessage(), e);
        }

        request.setAttribute("message", message);
        request.getRequestDispatcher("/login").forward(request, response);
    }

    private void handleUnauthorizedAccess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.warn("User is not logged in!");
        request.setAttribute("message",  "Error! You must log in to edit your profile.");
        request.getRequestDispatcher("/login").forward(request, response);
    }
}
package org.cinema.controller.general;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private static final String MESSAGE_PARAM = "message";
    private static final String ADMIN_REDIRECT_PATH = "/admin";
    private static final String USER_REDIRECT_PATH = "/user";

    private final UserService userService;

    @GetMapping
    public String showLoginPage() {
        log.debug("Handling GET request for login page...");
        return "login";
    }

    @PostMapping
    public String processLogin(@RequestParam("login") String username,
                               @RequestParam("password") String password,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for authorization...");

        try {
            UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                    .username(username)
                    .password(password)
                    .build();
            return processLoginAttempt(userUpdateDTO, request, redirectAttributes);
        } catch (IllegalArgumentException e) {
            log.warn("Login validation error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! " + e.getMessage());
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! An unexpected error occurred. Please try again later.");
            return "redirect:/login";
        }
    }

    private String processLoginAttempt(UserUpdateDTO userUpdateDTO, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            HttpSession session = userService.login(userUpdateDTO, request.getSession());
            String role = (String) session.getAttribute("role");

            if ("ADMIN".equals(role)) {
                log.info("Admin '{}' logged in successfully", userUpdateDTO.getUsername());
                return "redirect:" + ADMIN_REDIRECT_PATH;
            } else {
                log.info("User '{}' logged in successfully", userUpdateDTO.getUsername());
                return "redirect:" + USER_REDIRECT_PATH;
            }
        } catch (IllegalArgumentException e) {
            log.warn("Login failed for user '{}': {}", userUpdateDTO.getUsername(), e.getMessage());
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! " + e.getMessage());
            return "redirect:/login";
        }
    }
}
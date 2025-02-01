package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegisterController {

    private static final String MESSAGE_PARAM = "message";

    private final UserService userService;

    @GetMapping
    public String getRegistrationPage(@RequestParam(required = false) String message, Model model) {
        log.debug("Handling GET request for registration page...");

        if (message != null && !message.isEmpty()) {
            model.addAttribute(MESSAGE_PARAM, message);
        }
        return "registration";
    }

    @PostMapping
    public String registerUser(UserUpdateDTO userUpdateDTO, RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for registration page...");

        try {
            userService.register(userUpdateDTO);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            handleRegistrationError(redirectAttributes, "Invalid input: " + e.getMessage());
        } catch (EntityAlreadyExistException e) {
            handleRegistrationError(redirectAttributes, "User with this login already exists");
        } catch (Exception e) {
            handleRegistrationError(redirectAttributes, "An unexpected error occurred during registration");
        }
        return "redirect:/registration";
    }

    private void handleRegistrationError(RedirectAttributes redirectAttributes, String errorMessage) {
        redirectAttributes.addFlashAttribute(MESSAGE_PARAM, errorMessage);
    }
}

package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegisterController {

    private static final String MESSAGE_PARAM = "message";

    private final UserService userService;

    @GetMapping
    public String getRegistrationPage(@RequestParam(required = false) String message, Model model) {
        if (message != null && !message.isEmpty()) {
            model.addAttribute(MESSAGE_PARAM, message);
        }
        return "registration";
    }

    @PostMapping
    public String registerUser(UserUpdateDTO userUpdateDTO, RedirectAttributes redirectAttributes) {
        try {
            userService.register(userUpdateDTO);
            handleSuccessfulRegistration(redirectAttributes);
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

    private void handleSuccessfulRegistration(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Registration successful! Please login.");
    }

    private void handleRegistrationError(RedirectAttributes redirectAttributes, String errorMessage) {
        redirectAttributes.addFlashAttribute(MESSAGE_PARAM, errorMessage);
    }
}

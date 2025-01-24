package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/edit")
@RequiredArgsConstructor
public class UserEditController {

    private static final String MESSAGE_PARAM = "message";

    private final UserService userService;

    @GetMapping()
    public String showEditProfilePage(@RequestParam(required = false) String message, Model model, HttpSession session) {
        try {
            Long userId = getUserIdFromSession(session);
            UserResponseDTO user = userService.getById(String.valueOf(userId))
                    .orElseThrow(() -> new NoDataFoundException("User not found with ID: " + userId));

            model.addAttribute("user", user);
            if (message != null && !message.isEmpty()) {
                model.addAttribute(MESSAGE_PARAM, message);
            }

        } catch (IllegalArgumentException e) {
            model.addAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
            model.addAttribute("user", null);
        } catch (NoDataFoundException e) {
            model.addAttribute(MESSAGE_PARAM, "Error! " + e.getMessage());
            model.addAttribute("user", null);
        } catch (Exception e) {
            model.addAttribute(MESSAGE_PARAM, "An unexpected error occurred while loading the profile");
            model.addAttribute("user", null);
        }
        return "editProfile";
    }

    @PostMapping()
    public String updateProfile(@RequestParam String username,
                                @RequestParam(required = false) String password,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Long userId = getUserIdFromSession(session);

            if (password == null || password.trim().isEmpty()) {
                password = "null";
            }

            UserUpdateDTO userUpdateDTO = UserUpdateDTO.builder()
                    .username(username)
                    .password(password)
                    .build();

            userService.updateProfile(userId, userUpdateDTO);

            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Success! Profile updated successfully.");
            return "redirect:/user/edit";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
            return "redirect:/user/edit";
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! " + e.getMessage());
            return "redirect:/user/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "An unexpected error occurred while updating the profile");
            return "redirect:/user/edit";
        }
    }

    private Long getUserIdFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in session");
        }
        return userId;
    }
}
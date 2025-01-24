package org.cinema.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.userDTO.UserCreateDTO;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.service.UserService;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private static final String MESSAGE_PARAM = "message";

    private final UserService userService;

    @GetMapping
    public String showUsersPage(@RequestParam(value = "action", required = false) String action,
                                @RequestParam(value = "id", required = false) String userId,
                                Model model) {
        log.debug("Handling GET request for users...");

        try {
            if ("edit".equals(action)) {
                handleEditAction(userId, model);
            }

            loadDataForView(model);

            String message = (String) model.asMap().get(MESSAGE_PARAM);
            if (message != null && !message.isEmpty()) {
                model.addAttribute(MESSAGE_PARAM, message);
            }
        } catch (IllegalArgumentException e) {
            handleError(model, "Invalid input: " + e.getMessage(), e);
            setEmptyCollections(model);
        } catch (NoDataFoundException e) {
            handleError(model, e.getMessage(), e);
            setEmptyCollections(model);
        } catch (Exception e) {
            handleError(model, "An unexpected error occurred while fetching users", e);
            setEmptyCollections(model);
        }
        return "users";
    }

    @PostMapping
    public String processUserAction(@RequestParam String action,
                                    @RequestParam(required = false) String id,
                                    @RequestParam(required = false) String username,
                                    @RequestParam(required = false) String password,
                                    @RequestParam(required = false) String role,
                                    RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for users operations...");

        try {
            String message = processAction(action, id, username, password, role);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, message);
        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "Error! Invalid input: " + e.getMessage());
        } catch (NoDataFoundException | EntityAlreadyExistException e) {
            log.warn("Business error: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(MESSAGE_PARAM, "An unexpected error occurred");
        }

        return "redirect:/admin/users";
    }

    private String processAction(String action, String id, String username, String password, String role) {
        return switch (action) {
            case "add" -> handleAddAction(username, password, role);
            case "delete" -> handleDeleteAction(id);
            case "update" -> handleUpdateAction(id, username, password, role);
            default -> {
                log.warn("Unknown action requested: {}", action);
                yield "Unknown action requested";
            }
        };
    }

    private void loadDataForView(Model model) {
        log.debug("Loading data for view...");
        Set<UserResponseDTO> users = userService.findAll();
        model.addAttribute("users", users);
    }

    private void setEmptyCollections(Model model) {
        model.addAttribute("users", Collections.emptySet());
    }

    private String handleAddAction(String username, String password, String role) {
        UserCreateDTO userCreateDTO = UserCreateDTO.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        return userService.save(userCreateDTO);
    }

    private String handleUpdateAction(String id, String username, String password, String role) {
        Long userId = ValidationUtil.parseLong(id);
        UserCreateDTO userUpdateDTO = UserCreateDTO.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
        return userService.update(userId, userUpdateDTO);
    }

    private String handleDeleteAction(String id) {
        return userService.delete(id);
    }

    private void handleEditAction(String userId, Model model) {
        UserResponseDTO user = userService.getById(userId)
                .orElseThrow(() -> new NoDataFoundException("Error! User with ID " + userId + " doesn't exist."));
        model.addAttribute("user", user);
    }

    private void handleError(Model model, String message, Exception e) {
        log.error("{}: {}", message, e.getMessage(), e);
        model.addAttribute(MESSAGE_PARAM, message);
    }
}
package org.cinema.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cinema.constants.ParamConstant;
import org.cinema.constants.RedirectConstant;
import org.cinema.dto.userDTO.UserCreateDTO;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.exception.NoDataFoundException;
import org.cinema.handler.ErrorHandler;
import org.cinema.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String getUsers(Model model) {
        log.debug("Fetching all users...");
        model.addAttribute(ParamConstant.USERS_PARAM, userService.findAll());
        return ParamConstant.USERS_PARAM;
    }

    @GetMapping("/edit")
    public String getEditUser(@RequestParam(ParamConstant.ID_PARAM) String userId, Model model) {
        try {
            UserResponseDTO userToEdit = userService.getById(userId);
            model.addAttribute(ParamConstant.USER_TO_EDIT, userToEdit);
            model.addAttribute(ParamConstant.USERS_PARAM, userService.findAll());
        } catch (NoDataFoundException e) {
            log.error("User not found: {}", e.getMessage());
            model.addAttribute(ParamConstant.MESSAGE_PARAM, "Error! User not found.");
        }
        return ParamConstant.USERS_PARAM;
    }

    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute UserCreateDTO createDTO,
                          RedirectAttributes redirectAttributes) {
        try {
            String message = userService.save(createDTO);
            redirectAttributes.addFlashAttribute(ParamConstant.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return RedirectConstant.REDIRECT_ADMIN_USERS;
    }

    @PostMapping("/edit")
    public String editUser(@RequestParam Long id,
                           @Valid @ModelAttribute UserCreateDTO updateDTO,
                           RedirectAttributes redirectAttributes) {
        try {
            String message = userService.update(id, updateDTO);
            redirectAttributes.addFlashAttribute(ParamConstant.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return RedirectConstant.REDIRECT_ADMIN_USERS;
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam(ParamConstant.ID_PARAM) String userId,
                             RedirectAttributes redirectAttributes) {
        try {
            String message = userService.delete(userId);
            redirectAttributes.addFlashAttribute(ParamConstant.MESSAGE_PARAM, message);
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return RedirectConstant.REDIRECT_ADMIN_USERS;
    }
}
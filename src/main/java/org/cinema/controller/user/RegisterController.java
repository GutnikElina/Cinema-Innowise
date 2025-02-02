package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.constants.PageConstant;
import org.cinema.constants.ParamConstant;
import org.cinema.constants.RedirectConstant;
import org.cinema.service.UserService;
import org.cinema.handler.ErrorHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @GetMapping
    public String getRegistrationPage(@RequestParam(required = false) String message, Model model) {
        log.debug("Handling GET request for registration page...");

        if (StringUtils.isNotBlank(message)) {
            model.addAttribute(ParamConstant.MESSAGE_PARAM, message);
        }
        return PageConstant.REGISTRATION_PAGE;
    }

    @PostMapping
    public String registerUser(UserUpdateDTO userUpdateDTO, RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for registration page...");

        try {
            userService.register(userUpdateDTO);
            return RedirectConstant.REDIRECT_LOGIN;
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return RedirectConstant.REDIRECT_REGISTER;
    }
}
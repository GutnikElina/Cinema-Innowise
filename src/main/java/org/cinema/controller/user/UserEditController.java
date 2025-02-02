package org.cinema.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.handler.ErrorHandler;
import org.cinema.mapper.userMapper.UserUpdateMapper;
import org.cinema.service.UserService;
import org.cinema.constants.PageConstant;
import org.cinema.constants.ParamConstant;
import org.cinema.constants.RedirectConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/user/edit")
@RequiredArgsConstructor
public class UserEditController {

    private final UserService userService;

    @GetMapping
    public String showEditProfilePage(@RequestParam(required = false) String message,
                                      Model model, HttpSession session) {
        log.debug("Handling GET request for edit user data page...");

        try {
            Long userId = getUserIdFromSession(session);
            UserResponseDTO user = userService.getById(String.valueOf(userId));
            model.addAttribute(ParamConstant.USER_PARAM, user);

            if (StringUtils.isNotBlank(message)) {
                model.addAttribute(ParamConstant.MESSAGE_PARAM, message);
            }
        } catch (Exception e) {
            ErrorHandler.handleError(model, e);
        }

        return PageConstant.EDIT_PROFILE_PAGE;
    }

    @PostMapping
    public String updateProfile(@RequestParam String username,
                                @RequestParam(required = false) String password,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        log.debug("Handling POST request for edit user data page...");

        try {
            Long userId = getUserIdFromSession(session);
            password = StringUtils.defaultIfBlank(password, "null");
            UserUpdateDTO userUpdateDTO = UserUpdateMapper.INSTANCE.toDTO(username, password);
            userService.updateProfile(userId, userUpdateDTO);
            redirectAttributes.addFlashAttribute(ParamConstant.MESSAGE_PARAM,
                    "Success! Profile updated successfully.");
        } catch (Exception e) {
            ErrorHandler.handleError(redirectAttributes, ErrorHandler.resolveErrorMessage(e), e);
        }
        return RedirectConstant.REDIRECT_USER_EDIT;
    }

    private Long getUserIdFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute(ParamConstant.USER_ID_PARAM);
        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in session");
        }
        return userId;
    }
}
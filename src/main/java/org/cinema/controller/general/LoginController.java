package org.cinema.controller.general;

import lombok.extern.slf4j.Slf4j;
import org.cinema.constants.PageConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error) {
        log.info("Login page requested. Error parameter: {}", error);
        return PageConstant.LOGIN_PAGE;
    }
}
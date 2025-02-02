package org.cinema.controller.general;

import lombok.extern.slf4j.Slf4j;
import org.cinema.constants.PageConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        log.debug("Handling GET request for login page...");
        return PageConstant.LOGIN_PAGE;
    }
}
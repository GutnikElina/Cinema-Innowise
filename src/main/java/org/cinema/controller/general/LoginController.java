package org.cinema.controller.general;

import lombok.extern.slf4j.Slf4j;
import org.cinema.util.ConstantsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        log.debug("Handling GET request for login page...");
        return ConstantsUtil.LOGIN_PAGE;
    }
}
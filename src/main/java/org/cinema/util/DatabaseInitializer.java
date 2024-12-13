package org.cinema.util;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.service.UserService;
import org.cinema.service.impl.UserServiceImpl;

@Slf4j
@WebListener
public class DatabaseInitializer implements ServletContextListener {
    
    private final UserService userService = UserServiceImpl.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            initializeDefaultUsers();
        } catch (Exception e) {
            log.error("Failed to initialize default users", e);
        }
    }

    private void initializeDefaultUsers() {
        try {
            if (userService.findByUsername("admin").isEmpty()) {
                userService.save("admin", "admin", "ADMIN");
                log.info("Default admin user created successfully");
            }

            if (userService.findByUsername("user123").isEmpty()) {
                userService.save("user123", "user123", "USER");
                log.info("Default test user created successfully");
            }
        } catch (Exception e) {
            log.error("Error creating default users", e);
        }
    }
}

package org.cinema.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.repository.UserRepository;
import org.cinema.service.UserService;
import org.cinema.util.PasswordUtil;
import org.cinema.util.ValidationUtil;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UserServiceImpl implements UserService {

    @Getter
    private static final UserServiceImpl instance = new UserServiceImpl();

    private static final UserRepository userRepository = UserRepository.getInstance();

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public String save(String username, String password, String role) {
        try {
            ValidationUtil.validateUsername(username);
            ValidationUtil.validatePassword(password);
            ValidationUtil.validateRole(role);

            Role userRole = Role.valueOf(role.toUpperCase());
            User user = new User(username, PasswordUtil.hashPassword(password), userRole);

            userRepository.save(user);
            return "Success! User was successfully added!";
        } catch (IllegalArgumentException e) {
            log.error("Error adding user: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        }
    }

    @Override
    public String update(int userId, String username, String password, String role) {
        try {
            ValidationUtil.validateUsername(username);
            ValidationUtil.validatePassword(password);
            ValidationUtil.validateRole(role);

            Role userRole = Role.valueOf(role.toUpperCase());
            User existingUser = userRepository.getById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User with this ID doesn't exist!"));

            existingUser.setUsername(username);
            existingUser.setPassword(PasswordUtil.hashPassword(password));
            existingUser.setRole(userRole);

            userRepository.update(existingUser);
            return "Success! User was successfully updated!";
        } catch (IllegalArgumentException e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            return "Error! " + e.getMessage();
        }
    }

    @Override
    public String delete(int userId) {
        try {
            userRepository.delete(userId);
            return "Success! User was successfully deleted!";
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            return "Error! Could not delete user.";
        }
    }

    @Override
    public Optional<User> getById(int userId) {
        return userRepository.getById(userId);
    }

    @Override
    public HttpSession auth(String username, String password, HttpSession session) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }

        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole().toString());
        return session;
    }

    @Override
    public void register(String username, String password) {
        try {
            ValidationUtil.validateUsername(username);
            ValidationUtil.validatePassword(password);

            if (userRepository.getByUsername(username).isPresent()) {
                throw new IllegalArgumentException("Username already exists. Please choose another one.");
            }

            User user = new User(username, PasswordUtil.hashPassword(password), Role.USER);
            userRepository.save(user);

            log.info("User [{}] registered successfully.", username);
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage());
            throw new IllegalArgumentException("Registration failed: " + e.getMessage());
        }
    }
}

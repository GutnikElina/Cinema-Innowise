package org.cinema.service.impl;

import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.repository.UserRepository;
import org.cinema.service.UserService;
import org.cinema.util.PasswordUtil;
import org.cinema.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final UserRepository userRepository = UserRepository.getInstance();
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

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
}

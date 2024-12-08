package org.cinema.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.error.EntityAlreadyExistException;
import org.cinema.error.NoDataFoundException;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.repository.impl.UserRepositoryImpl;
import org.cinema.service.UserService;
import org.cinema.util.PasswordUtil;
import org.cinema.util.ValidationUtil;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class UserServiceImpl implements UserService {

    @Getter
    private static final UserServiceImpl instance = new UserServiceImpl();

    private final UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();

    @Override
    public String save(String username, String password, String role) {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);
        ValidationUtil.validateRole(role);

        if (userRepository.getByUsername(username).isPresent()) {
            throw new EntityAlreadyExistException("Username already exists. Please choose another one.");
        }

        Role userRole = Role.valueOf(role.toUpperCase());
        User user = new User(username, PasswordUtil.hashPassword(password), userRole);
        userRepository.save(user);

        if (userRepository.getByUsername(username).isPresent()) {
            throw new NoDataFoundException("User not found in database after adding. Try again.");
        }

        log.info("User '{}' successfully added with role '{}'.", username, userRole);
        return "User '" + username + "' successfully added!";
    }

    @Override
    public String update(String userId, String username, String password, String role) {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);
        ValidationUtil.validateRole(role);

        User existingUser = userRepository.getById(ValidationUtil.parseId(userId))
                .orElseThrow(() -> new NoDataFoundException("User with ID " + userId + " doesn't exist."));

        if (!existingUser.getUsername().equals(username) && userRepository.getByUsername(username).isPresent()) {
            throw new EntityAlreadyExistException("Username '" + username + "' is already taken.");
        }

        existingUser.setUsername(username);
        existingUser.setPassword(PasswordUtil.hashPassword(password));
        existingUser.setRole(Role.valueOf(role.toUpperCase()));

        userRepository.update(existingUser);

        if (userRepository.getByUsername(username).isPresent()) {
            throw new NoDataFoundException("User not found in database after updating. Try again.");
        }

        log.info("User with ID {} successfully updated.", userId);
        return "User with ID " + userId + " successfully updated!";
    }

    @Override
    public String delete(String userIdStr) {
        int userId = ValidationUtil.parseId(userIdStr);
        ValidationUtil.validateIsPositive(userId);
        userRepository.delete(userId);
        return "Success! User was successfully deleted!";
    }

    @Override
    public Optional<User> getById(String userIdStr) {
        int userId = ValidationUtil.parseId(userIdStr);
        ValidationUtil.validateIsPositive(userId);
        return userRepository.getById(userId);
    }

    @Override
    public Set<User> findAll() {
        Set<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NoDataFoundException("No users found in the database.");
        }

        log.info("{} users retrieved successfully.", users.size());
        return users;
    }

    @Override
    public HttpSession login(String username, String password, HttpSession session) {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);

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
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);

        if (userRepository.getByUsername(username).isPresent()) {
            throw new EntityAlreadyExistException("Username already exists. Please choose another one.");
        }

        User user = new User(username, PasswordUtil.hashPassword(password), Role.USER);
        userRepository.save(user);

        if (userRepository.getByUsername(username).isPresent()) {
            throw new NoDataFoundException("User not found in database after registration. Try again.");
        }

        log.info("User '{}' registered successfully.", username);
    }
}

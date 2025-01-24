package org.cinema.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.dto.userDTO.UserCreateDTO;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.exception.EntityAlreadyExistException;
import org.cinema.exception.NoDataFoundException;
import org.cinema.mapper.userMapper.UserCreateMapper;
import org.cinema.mapper.userMapper.UserResponseMapper;
import org.cinema.mapper.userMapper.UserUpdateMapper;
import org.cinema.model.Role;
import org.cinema.model.User;
import org.cinema.repository.impl.UserRepositoryImpl;
import org.cinema.service.UserService;
import org.cinema.util.PasswordUtil;
import org.cinema.util.ValidationUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class UserServiceImpl implements UserService {

    @Getter
    private static final UserServiceImpl instance = new UserServiceImpl();

    private final UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();

    @Override
    public String save(UserCreateDTO userCreateDTO) {
        ValidationUtil.validateUsername(userCreateDTO.getUsername());
        ValidationUtil.validatePassword(userCreateDTO.getPassword());
        ValidationUtil.validateRole(userCreateDTO.getRole());

        if (userRepository.getByUsername(userCreateDTO.getUsername()).isPresent()) {
            throw new EntityAlreadyExistException("Username already exists. Please choose another one.");
        }

        User user = UserCreateMapper.INSTANCE.toEntity(userCreateDTO);
        user.setPassword(PasswordUtil.hashPassword(userCreateDTO.getPassword()));
        userRepository.save(user);

        if (userRepository.getByUsername(userCreateDTO.getUsername()).isEmpty()) {
            throw new NoDataFoundException("User not found in database after saving. Try again.");
        }

        log.info("User '{}' successfully added with role '{}'.", userCreateDTO.getUsername(), user.getRole());
        return String.format("User with username %s successfully added!", userCreateDTO.getUsername());
    }

    @Override
    public String update(Long userId, UserCreateDTO userUpdateDTO) {
        ValidationUtil.validateUsername(userUpdateDTO.getUsername());
        ValidationUtil.validatePassword(userUpdateDTO.getPassword());

        User existingUser = userRepository.getById(userId)
                .orElseThrow(() -> new NoDataFoundException("User with ID " + userId + " doesn't exist."));

        if (!existingUser.getUsername().equals(userUpdateDTO.getUsername()) &&
                userRepository.getByUsername(userUpdateDTO.getUsername()).isPresent()) {
            throw new EntityAlreadyExistException("Username '" + userUpdateDTO.getUsername() + "' is already taken.");
        }

        User userToUpdate = UserCreateMapper.INSTANCE.toEntity(userUpdateDTO);
        existingUser.setUsername(userToUpdate.getUsername());
        existingUser.setPassword(PasswordUtil.hashPassword(userUpdateDTO.getPassword()));
        existingUser.setRole(Role.valueOf(userUpdateDTO.getRole()));

        userRepository.update(existingUser);

        if (userRepository.getByUsername(userToUpdate.getUsername()).isEmpty()) {
            throw new NoDataFoundException("User not found in database after updating. Try again.");
        }

        log.info("User with ID {} successfully updated.", userId);
        return String.format("User with ID %s successfully updated!", userId);
    }

    @Override
    public String delete(String userIdStr) {
        userRepository.delete(ValidationUtil.parseLong(userIdStr));
        return "Success! User was successfully deleted!";
    }

    @Override
    public Optional<UserResponseDTO> getById(String userIdStr) {
        return userRepository.getById(ValidationUtil.parseLong(userIdStr))
                .map(UserResponseMapper.INSTANCE::toDTO);
    }

    @Override
    public Set<UserResponseDTO> findAll() {
        Set<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NoDataFoundException("No users found in the database.");
        }

        log.info("{} users retrieved successfully.", users.size());
        return users.stream()
                .map(UserResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public HttpSession login(UserUpdateDTO userUpdateDTO, HttpSession session) {
        ValidationUtil.validateUsername(userUpdateDTO.getUsername());
        ValidationUtil.validatePassword(userUpdateDTO.getPassword());

        User user = userRepository.getByUsername(userUpdateDTO.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));

        if (!PasswordUtil.checkPassword(userUpdateDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole().toString());
        return session;
    }

    @Override
    public void register(UserUpdateDTO userCreateDTO) {
        ValidationUtil.validateUsername(userCreateDTO.getUsername());
        ValidationUtil.validatePassword(userCreateDTO.getPassword());

        if (userRepository.getByUsername(userCreateDTO.getUsername()).isPresent()) {
            throw new EntityAlreadyExistException("Username already exists. Please choose another one.");
        }

        User user = UserUpdateMapper.INSTANCE.toEntity(userCreateDTO);
        user.setPassword(PasswordUtil.hashPassword(userCreateDTO.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        if (userRepository.getByUsername(userCreateDTO.getUsername()).isEmpty()) {
            throw new NoDataFoundException("User not found in database after registration. Try again.");
        }

        log.info("User '{}' registered successfully.", userCreateDTO.getUsername());
    }

    @Override
    public void updateProfile(long userId, UserUpdateDTO userUpdateDTO) {
        ValidationUtil.validateIsPositive((int) userId);

        User user = userRepository.getById(userId)
                .orElseThrow(() -> new NoDataFoundException("User with ID " + userId + " not found."));

        if (!user.getUsername().equals(userUpdateDTO.getUsername()) && userRepository.getByUsername(userUpdateDTO.getUsername()).isPresent()) {
            throw new EntityAlreadyExistException("Username '" + userUpdateDTO.getUsername() + "' is already taken.");
        }

        if (!Objects.equals(userUpdateDTO.getPassword(), "null")) {
            ValidationUtil.validatePassword(userUpdateDTO.getPassword());
            user.setPassword(PasswordUtil.hashPassword(userUpdateDTO.getPassword()));
        }

        user.setUsername(userUpdateDTO.getUsername());
        userRepository.update(user);
        log.info("User with ID {} updated their profile.", userId);
    }
}

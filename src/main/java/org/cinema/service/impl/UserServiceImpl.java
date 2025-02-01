package org.cinema.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import org.cinema.repository.UserRepository;
import org.cinema.service.UserService;
import org.cinema.util.PasswordUtil;
import org.cinema.util.ValidationUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public String save(UserCreateDTO userCreateDTO) {
        validateUser(userCreateDTO.getUsername(), userCreateDTO.getPassword());
        checkExistence(userCreateDTO.getUsername());

        User user = UserCreateMapper.INSTANCE.toEntity(userCreateDTO);
        user.setPassword(PasswordUtil.hashPassword(userCreateDTO.getPassword()));
        userRepository.save(user);

        log.info("User '{}' successfully added with role '{}'.", userCreateDTO.getUsername(), user.getRole());
        return String.format("Success! User with username %s successfully added!", userCreateDTO.getUsername());
    }

    @Override
    @Transactional
    public String update(Long userId, UserCreateDTO userUpdateDTO) {
        validateUser(userUpdateDTO.getUsername(), userUpdateDTO.getPassword());

        User existingUser = getUserById(userId);
        checkUsernameAvailability(existingUser.getUsername(), userUpdateDTO.getUsername());

        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setPassword(PasswordUtil.hashPassword(userUpdateDTO.getPassword()));
        existingUser.setRole(Role.valueOf(userUpdateDTO.getRole()));

        userRepository.save(existingUser);
        log.info("User with ID '{}' successfully updated.", userId);
        return String.format("Success! User with ID %s successfully updated!", userId);
    }

    @Override
    @Transactional
    public String delete(String userIdStr) {
        Long userId = ValidationUtil.parseLong(userIdStr);
        userRepository.deleteById(userId);
        return "Success! User was successfully deleted!";
    }

    @Override
    public UserResponseDTO getById(String userIdStr) {
        Long userId = ValidationUtil.parseLong(userIdStr);
        return userRepository.findById(userId)
                .map(UserResponseMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NoDataFoundException("Error! User with ID " + userId + " doesn't exist."));
    }

    @Override
    public List<UserResponseDTO> findAll() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NoDataFoundException("Error! No users found in the database.");
        }

        log.info("{} users retrieved successfully.", users.size());
        return users.stream()
                .map(UserResponseMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void register(UserUpdateDTO userCreateDTO) {
        validateUser(userCreateDTO.getUsername(), userCreateDTO.getPassword());
        checkExistence(userCreateDTO.getUsername());

        User user = UserUpdateMapper.INSTANCE.toEntity(userCreateDTO);
        user.setPassword(PasswordUtil.hashPassword(userCreateDTO.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        log.info("User '{}' registered successfully.", userCreateDTO.getUsername());
    }

    @Override
    @Transactional
    public void updateProfile(long userId, UserUpdateDTO userUpdateDTO) {
        ValidationUtil.validateIsPositive((int) userId);
        User user =  getUserById(userId);
        checkUsernameAvailability(user.getUsername(), userUpdateDTO.getUsername());

        if (!Objects.equals(userUpdateDTO.getPassword(), "null")) {
            ValidationUtil.validatePassword(userUpdateDTO.getPassword());
            user.setPassword(PasswordUtil.hashPassword(userUpdateDTO.getPassword()));
        }
        user.setUsername(userUpdateDTO.getUsername());
        userRepository.save(user);
        log.info("User with ID '{}' updated their profile.", userId);
    }

    @Override
    public UserResponseDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponseMapper.INSTANCE::toDTO)
                .orElseThrow(() -> new NoDataFoundException("Error! User not found: " + username));
    }

    private void checkExistence(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new EntityAlreadyExistException("Error! Username already exists. Please choose another one.");
        }
    }

    private void validateUser(String username, String password) {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoDataFoundException("Error! User with ID '" + userId + "' not found."));
    }

    private void checkUsernameAvailability(String currentUsername, String newUsername) {
        if (!currentUsername.equals(newUsername) && userRepository.findByUsername(newUsername).isPresent()) {
            throw new EntityAlreadyExistException("Error! Username '" + newUsername + "' is already taken.");
        }
    }
}

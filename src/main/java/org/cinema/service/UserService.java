package org.cinema.service;

import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.dto.userDTO.UserCreateDTO;
import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService {
    /**
     * Creates a new user.
     *
     * @param userCreateDTO the DTO containing user details.
     * @return a message indicating successful creation.
     */
    String save(UserCreateDTO userCreateDTO);

    /**
     * Updates an existing user.
     *
     * @param userId the ID of the user to update.
     * @param userUpdateDTO the DTO containing updated user details.
     * @return a message indicating successful update.
     */
    String update(Long userId, UserCreateDTO userUpdateDTO);

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete.
     * @return a message indicating successful deletion.
     */
    String delete(String userId);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve.
     * @return containing the {@link UserResponseDTO}, if found.
     */
    UserResponseDTO getById(String userId);

    /**
     * Retrieves all users.
     *
     * @return a list of {@link UserResponseDTO}.
     */
    List<UserResponseDTO> findAll();

    /**
     * Registers a new user.
     *
     * @param userCreateDTO the DTO containing registration details.
     */
    void register(UserUpdateDTO userCreateDTO);

    /**
     * Updates the profile of an existing user.
     *
     * @param userId the ID of the user to update.
     * @param userUpdateDTO the DTO containing updated profile details.
     */
    void updateProfile(long userId, UserUpdateDTO userUpdateDTO);

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user.
     * @return containing the {@link UserResponseDTO}, if found.
     */
    UserResponseDTO findByUsername(String username);
}

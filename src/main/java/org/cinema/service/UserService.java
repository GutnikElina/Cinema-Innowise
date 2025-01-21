package org.cinema.service;

import jakarta.servlet.http.HttpSession;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.dto.userDTO.UserCreateDTO;
import java.util.Optional;
import java.util.Set;

/**
 * Service interface for managing users.
 */
public interface UserService {
    /**
     * Creates a new user.
     *
     * @param userCreateDTO the DTO containing user details.
     * @return the ID of the created user.
     */
    String save(UserCreateDTO userCreateDTO);

    /**
     * Updates an existing user.
     *
     * @param userId the ID of the user to update.
     * @param userUpdateDTO the DTO containing updated user details.
     * @return the ID of the updated user.
     */
    String update(Long userId, UserCreateDTO userUpdateDTO);

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete.
     * @return the ID of the deleted user.
     */
    String delete(String userId);

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve.
     * @return an {@link Optional} containing the {@link UserResponseDTO}, if found.
     */
    Optional<UserResponseDTO> getById(String userId);

    /**
     * Retrieves all users.
     *
     * @return a set of {@link UserResponseDTO}.
     */
    Set<UserResponseDTO> findAll();

    /**
     * Logs a user in.
     *
     * @param userUpdateDTO the DTO containing login details.
     * @param session the current HTTP session.
     * @return the updated HTTP session.
     */
    HttpSession login(UserUpdateDTO userUpdateDTO, HttpSession session);

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
     * @param userCreateDTO the DTO containing updated profile details.
     */
    void updateProfile(long userId, UserUpdateDTO userCreateDTO);
}

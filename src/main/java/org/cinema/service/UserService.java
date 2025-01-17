package org.cinema.service;

import jakarta.servlet.http.HttpSession;
import org.cinema.dto.userDTO.UserResponseDTO;
import org.cinema.dto.userDTO.UserUpdateDTO;
import org.cinema.dto.userDTO.UserCreateDTO;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    String save(UserCreateDTO userCreateDTO);
    String update(Long userId, UserCreateDTO userUpdateDTO);
    String delete(String userId);
    Optional<UserResponseDTO> getById(String userId);
    Set<UserResponseDTO> findAll();
    HttpSession login(UserUpdateDTO userUpdateDTO, HttpSession session);
    void register(UserUpdateDTO userCreateDTO);
    void updateProfile(long userId, UserUpdateDTO userCreateDTO);
}

package org.cinema.service;

import jakarta.servlet.http.HttpSession;
import org.cinema.dto.UserDTO;
import org.cinema.model.User;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    String save(String username, String password, String role);
    String update(String userId, String username, String password, String role);
    String delete(String userId);
    Optional<UserDTO> getById(String userId);
    Set<UserDTO> findAll();
    HttpSession login(String username, String password, HttpSession session);
    void register(String username, String password);
    void updateProfile(long userId, String username, String password);
}

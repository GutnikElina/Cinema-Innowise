package org.cinema.service;

import jakarta.servlet.http.HttpSession;
import org.cinema.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    String save(String username, String password, String role);
    String update(int userId, String username, String password, String role);
    String delete(int userId);
    Optional<User> getById(int userId);
    HttpSession auth(String username, String password, HttpSession session);
    void register(String username, String password);
}

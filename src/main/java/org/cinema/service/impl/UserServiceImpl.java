package org.cinema.service.impl;

import lombok.Getter;
import org.cinema.model.User;
import org.cinema.repository.UserRepository;
import org.cinema.service.UserService;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Getter
    private static final UserServiceImpl instance = new UserServiceImpl();

    private static final UserRepository userRepository = UserRepository.getInstance();

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }
}

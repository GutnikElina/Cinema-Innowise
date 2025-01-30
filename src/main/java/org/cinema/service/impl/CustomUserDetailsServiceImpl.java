package org.cinema.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.cinema.model.User;
import org.cinema.repository.UserRepository;
import org.cinema.service.CustomUserDetailsService;
import org.cinema.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsServiceImpl
        implements UserDetailsService, CustomUserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username" + username +" not found!"));

        log.info("Loaded user: {} with role: {}", user.getUsername(), user.getRole());
        return new UserWrapper(user);
    }
}

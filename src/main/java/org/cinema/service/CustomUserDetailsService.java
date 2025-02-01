package org.cinema.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for loading user-specific data.
 * This interface is used by Spring Security to retrieve user details
 * based on the username during authentication.
 */
public interface CustomUserDetailsService {
    /**
     * Loads the user details based on the given username.
     *
     * @param username the username identifying the user whose data is required
     * @return UserDetails containing user authentication and authorization information
     */
    UserDetails loadUserByUsername(String username);
}
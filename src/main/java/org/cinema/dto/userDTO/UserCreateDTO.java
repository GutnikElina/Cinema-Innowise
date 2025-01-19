package org.cinema.dto.userDTO;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for creating a new user.
 */
@Data
@Builder
public class UserCreateDTO {
    private String username;
    private String password;
    private String role;
}
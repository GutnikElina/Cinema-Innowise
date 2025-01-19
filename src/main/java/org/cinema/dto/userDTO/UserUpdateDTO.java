package org.cinema.dto.userDTO;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for updating user details.
 */
@Data
@Builder
public class UserUpdateDTO {
    private String username;
    private String password;
}
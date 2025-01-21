package org.cinema.dto.userDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDTO {
    private String username;
    private String password;
    private String role;
}
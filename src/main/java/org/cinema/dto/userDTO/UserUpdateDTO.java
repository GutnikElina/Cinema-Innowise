package org.cinema.dto.userDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDTO {
    private String username;
    private String password;
}
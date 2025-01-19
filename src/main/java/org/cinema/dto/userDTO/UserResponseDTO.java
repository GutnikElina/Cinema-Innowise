package org.cinema.dto.userDTO;

import lombok.Builder;
import lombok.Data;
import org.cinema.model.Role;
import java.time.LocalDateTime;

/**
 * DTO for representing user details in the response.
 */
@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private Role role;
    private LocalDateTime createdAt;
}

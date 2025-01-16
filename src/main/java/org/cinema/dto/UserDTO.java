package org.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.cinema.model.Role;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private Role role;
    private LocalDateTime createdAt;
}

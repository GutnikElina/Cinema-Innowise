package org.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public User(String username, String password, String salt, Role role) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.role = role;
    }

    @PrePersist
    private void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

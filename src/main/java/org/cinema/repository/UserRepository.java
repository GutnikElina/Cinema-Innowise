package org.cinema.repository;

import org.cinema.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user.
     * @return an {@link Optional} containing the user, or empty if not found.
     */
    Optional<User> findByUsername(String username);
}

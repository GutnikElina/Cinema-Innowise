package org.cinema.repository.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.cinema.config.HibernateConfig;
import org.cinema.model.User;
import org.cinema.repository.AbstractHibernateRepository;
import org.cinema.repository.UserRepository;
import org.hibernate.query.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class UserRepositoryImpl extends AbstractHibernateRepository<User> implements UserRepository {

    @Getter
    private static final UserRepositoryImpl instance = new UserRepositoryImpl();

    public UserRepositoryImpl() {
        super(HibernateConfig.getSessionFactory(), User.class);
    }

    @Override
    public void save(User user) {
        super.save(user);
        log.info("User '{}' successfully added.", user.getUsername());
    }

    @Override
    public void update(User user) {
        super.update(user);
        log.info("User with ID '{}' successfully updated.", user.getId());
    }

    @Override
    public void delete(long id) {
        super.delete(id);
    }

    @Override
    public Optional<User> getById(long id) {
        return super.getById(id);
    }

    @Override
    public Set<User> findAll() {
        return executeWithResult(session -> {
            log.debug("Retrieving all users...");
            List<User> users = session.createQuery(
                "FROM User u ORDER BY u.createdAt ASC", 
                User.class
            ).list();
            log.info("{} users successfully retrieved.", users.size());
            return new HashSet<>(users);
        });
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return executeWithResult(session -> {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResultOptional();
        });
    }
}

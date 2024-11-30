package org.cinema.dao;

import org.cinema.models.User;
import java.util.List;

public class UserDAO extends BaseDao implements Repository<User>{
    @Override
    public void add(User entity) {

    }

    @Override
    public User getById(int id) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return List.of();
    }

    @Override
    public void update(User entity) {

    }

    @Override
    public void delete(int id) {

    }
}

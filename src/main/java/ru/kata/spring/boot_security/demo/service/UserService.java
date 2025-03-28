package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;


public interface UserService {
    List<User> listAllUsers();

    User getUserById(int id);

    void save(User user);

    void update(User user);

    void deleteUserById(int id);

    User findUserByUsername(String username);
}

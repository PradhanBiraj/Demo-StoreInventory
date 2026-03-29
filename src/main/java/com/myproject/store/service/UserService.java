package com.myproject.store.service;

import com.myproject.store.dto.UserRegistrationDto;
import com.myproject.store.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User createUser(String username, String rawPassword, String roleKey, String name, String email, String contact);

    User registerUser(UserRegistrationDto registrationDto);

    void deleteUser(Long id);
}

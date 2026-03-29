package com.myproject.store.service.impl;

import com.myproject.store.dto.UserRegistrationDto;
import com.myproject.store.model.Role;
import com.myproject.store.model.User;
import com.myproject.store.repository.RoleRepository;
import com.myproject.store.repository.UserRepository;
import com.myproject.store.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User createUser(String username, String rawPassword, String roleKey, String name, String email,
            String contact) {
        String roleName = "ROLE_" + roleKey; // ADMIN -> ROLE_ADMIN, STAFF -> ROLE_STAFF

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setName(name);
        user.setEmail(email);
        user.setContact(contact);
        user.setEnabled(true);
        user.addRole(role);

        return userRepository.save(user);
    }

    @Override
    public User registerUser(UserRegistrationDto dto) {
        return createUser(dto.getUsername(), dto.getPassword(), dto.getRoleKey(), dto.getName(), dto.getEmail(),
                dto.getContact());
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

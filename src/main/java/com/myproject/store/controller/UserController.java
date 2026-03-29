package com.myproject.store.controller;

import com.myproject.store.model.User;
import com.myproject.store.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("username", "");
        model.addAttribute("password", "");
        model.addAttribute("roleKey", "STAFF");
        model.addAttribute("name", "");
        model.addAttribute("email", "");
        model.addAttribute("contact", "");
        return "users/form";
    }

    @PostMapping
    public String createUser(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("roleKey") String roleKey,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("contact") String contact,
            Model model) {

        try {
            userService.createUser(username, password, roleKey, name, email, contact);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("password", "");
            model.addAttribute("roleKey", roleKey);
            model.addAttribute("name", name);
            model.addAttribute("email", email);
            model.addAttribute("contact", contact);
            return "users/form";
        }

        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}

package com.myproject.store.controller;

import com.myproject.store.model.User;
import com.myproject.store.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private Model model;

    @BeforeEach
    void setUp() {
        model = new ConcurrentModel();
    }

    @Test
    void testListUsers() {
        when(userService.findAll()).thenReturn(Collections.emptyList());
        String view = userController.listUsers(model);
        assertEquals("users/list", view);
    }

    @Test
    void testShowCreateForm() {
        String view = userController.showCreateForm(model);
        assertEquals("users/form", view);
    }

    @Test
    void testCreateUserSuccess() {
        when(userService.createUser("user", "pass", "ADMIN", "Name", "email@e.com", "123"))
                .thenReturn(new User());

        String view = userController.createUser("user", "pass", "ADMIN", "Name", "email@e.com", "123", model);
        assertEquals("redirect:/users", view);
    }

    @Test
    void testCreateUserFailure() {
        when(userService.createUser(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Error!"));

        String view = userController.createUser("user", "pass", "ADMIN", "Name", "email@e.com", "123", model);
        assertEquals("users/form", view);
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userService).deleteUser(1L);
        String view = userController.deleteUser(1L);
        assertEquals("redirect:/users", view);
        verify(userService, times(1)).deleteUser(1L);
    }
}

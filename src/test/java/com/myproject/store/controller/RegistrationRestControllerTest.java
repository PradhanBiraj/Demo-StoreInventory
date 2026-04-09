package com.myproject.store.controller;

import com.myproject.store.dto.UserRegistrationDto;
import com.myproject.store.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private RegistrationRestController controller;

    @Test
    void testRegister() {
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(new com.myproject.store.model.User());
        
        ResponseEntity<?> resp = controller.registerUser(new UserRegistrationDto());
        assertNotNull(resp);
    }
}

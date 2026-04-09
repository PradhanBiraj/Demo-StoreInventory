package com.myproject.store.security;

import com.myproject.store.model.Role;
import com.myproject.store.model.User;
import com.myproject.store.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void testLoadUserByUsernameFound() {
        User u = new User();
        u.setUsername("test");
        u.setPassword("pass");
        u.setEnabled(true);
        u.addRole(new Role("ROLE_ADMIN"));
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(u));

        UserDetails details = userDetailsService.loadUserByUsername("test");
        assertNotNull(details);
        assertEquals("test", details.getUsername());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("notfound"));
    }
}

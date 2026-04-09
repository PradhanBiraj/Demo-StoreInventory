package com.myproject.store.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    void testJwtUtilMethods() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();
        
        java.lang.reflect.Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "mySuperSecretKeyThatIsVeryLongAndSecureForHS256Algorithm");
        
        java.lang.reflect.Field expField = JwtUtil.class.getDeclaredField("jwtExpirationInMs");
        expField.setAccessible(true);
        expField.set(jwtUtil, 3600000L); // 1 hour

        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);

        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);

        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));

        boolean isValid = jwtUtil.validateToken(token, userDetails);
        assertTrue(isValid);
    }
}

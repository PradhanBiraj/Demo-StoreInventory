package com.myproject.store.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtAuthenticationEntryPointTest {

    @Test
    void testCommence() throws Exception {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        entryPoint.commence(request, response, new AuthenticationException("Error") {});
        
        assertEquals(401, response.getStatus()); // wait, it might send redirect or 401. Typically sends 401 error.
    }
}

package com.myproject.store.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        String acceptHeader = request.getHeader("Accept");

        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            if (request.getMethod().equalsIgnoreCase("GET")) {
                // Navigation: User is not logged in, send to login page
                response.sendRedirect("/login");
            } else {
                // Form/Function trigger: User is not logged in, show alert then redirect to
                // login
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><script type='text/javascript'>" +
                        "alert('Security Alert: Your session has expired or you are not logged in. Please log in again.');"
                        +
                        "window.history.back();" +
                        "</script></body></html>");
            }
        } else {
            // API request - send 401 Unauthorized
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
}

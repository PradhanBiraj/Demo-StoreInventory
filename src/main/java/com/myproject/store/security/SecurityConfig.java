package com.myproject.store.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                // Public
                                                .requestMatchers("/css/**", "/js/**", "/login", "/register",
                                                                "/registration-success", "/api/auth/**",
                                                                "/api/register/**",
                                                                "/error/**")
                                                .permitAll()

                                                // Shared: ADMIN + STAFF
                                                .requestMatchers("/", "/dashboard").hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers("/items").hasAnyRole("ADMIN", "STAFF")
                                                .requestMatchers("/stock/in/**", "/stock/out/**")
                                                .hasAnyRole("ADMIN", "STAFF")

                                                // Admin-only areas
                                                .requestMatchers("/users/**").hasRole("ADMIN")
                                                .requestMatchers("/categories/**").hasRole("ADMIN")
                                                .requestMatchers("/suppliers/**").hasRole("ADMIN")

                                                // Admin-only operations on items & other stock actions
                                                .requestMatchers("/items/**").hasRole("ADMIN")
                                                .requestMatchers("/stock/**").hasRole("ADMIN")

                                                
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/dashboard", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                                        if (request.getMethod().equalsIgnoreCase("GET")) {
                                                                response.sendRedirect("/error/401");
                                                        } else {
                                                                response.setContentType("text/html;charset=UTF-8");
                                                                response.getWriter().write(
                                                                                "<html><body><script type='text/javascript'>"
                                                                                                +
                                                                                                "alert('Access Denied: You do not have the required permissions for this operation.');"
                                                                                                +
                                                                                                "window.history.back();"
                                                                                                +
                                                                                                "</script></body></html>");
                                                        }
                                                }));

                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }
}

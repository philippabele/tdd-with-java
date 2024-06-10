package com.todoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Configuring security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure CSRF protection
                .csrf(csrf -> csrf.disable())
                // Configure authorization rules
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/register").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint()); // Fügt einen AuthenticationEntryPoint hinzu

        return http.build();
    }

    // Bean definition for password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        // Sendet 401 Unauthorized statt 403 Forbidden bei fehlender Authentifizierung
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }
}

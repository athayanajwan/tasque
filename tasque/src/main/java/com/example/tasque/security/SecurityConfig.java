/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Athaya
 */
package com.example.tasque.security;

import com.example.tasque.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // Halaman publik
                .requestMatchers(
                    "/auth.html", "/dashboard.html", "/edit-profile.html","/project-detail.html",
                    "/js/**", "/css/**", "/images/**", "/assets/**", "/task-detail.html", "/notifications.html", 
                    "/assets/**", "/assets/notif2.gif"
                ).permitAll()

                // Endpoint publik
                .requestMatchers("/api/users/register", "/api/users/login").permitAll()

                // Endpoint yang memerlukan token
                .requestMatchers("/api/users/**", "/api/users/me", "/api/projects/**", "/api/projects/{projectId}/members",
                        "/api/projects/{projectId}","/api/tasks/**","/api/tasks/{taskId}", "/api/tasks/{taskId}/comments",
                        "/api/tasks/{taskId}/comments/{commentId}", "/api/notifications/**", "/api/notifications/**",
                        "/api/notifications/{id}/read", "/api/notifications"
                        ).authenticated()
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}


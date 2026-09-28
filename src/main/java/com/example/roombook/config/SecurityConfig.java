package com.example.roombook.config;

import com.example.roombook.model.User;
import com.example.roombook.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService() {

        return email -> {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found"
                            )
                    );

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                .csrf(csrf -> csrf.disable())


                .authorizeHttpRequests(auth -> auth

                        // Login
                        .requestMatchers("/login")
                        .permitAll()


                        // Dashboard
                        .requestMatchers("/")
                        .authenticated()


                        // =========================
                        // ROOMS
                        // =========================

                        // Both Admin and Customer can view rooms
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/rooms",
                                "/api/rooms/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "CUSTOMER"
                        )


                        // Only Admin can add/update/delete rooms
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/rooms/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/rooms/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/rooms/**"
                        )
                        .hasRole("ADMIN")


                        // Room pages
                        .requestMatchers("/rooms")
                        .hasAnyRole(
                                "ADMIN",
                                "CUSTOMER"
                        )

                        .requestMatchers("/add-room")
                        .hasRole("ADMIN")


                        // =========================
                        // EMPLOYEES
                        // =========================

                        // Both roles can view employees
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/employees",
                                "/api/employees/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "CUSTOMER"
                        )


                        // Only Admin can modify employees
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/employees/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/employees/**"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/employees/**"
                        )
                        .hasRole("ADMIN")


                        // Employee page
                        .requestMatchers(
                                "/employees",
                                "/add-employee"
                        )
                        .hasRole("ADMIN")


                        // =========================
                        // BOOKINGS
                        // =========================

                        // Both roles can use booking APIs
                        .requestMatchers(
                                "/api/bookings/**"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "CUSTOMER"
                        )


                        // Booking pages
                        .requestMatchers(
                                "/bookings",
                                "/add-booking"
                        )
                        .hasAnyRole(
                                "ADMIN",
                                "CUSTOMER"
                        )


                        // Everything else
                        .anyRequest()
                        .authenticated()
                )


                // =========================
                // LOGIN
                // =========================

                .formLogin(form -> form

                        .loginPage("/login")

                        .defaultSuccessUrl(
                                "/",
                                true
                        )

                        .failureUrl(
                                "/login?error=true"
                        )

                        .permitAll()
                )


                // =========================
                // LOGOUT
                // =========================

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )

                        .permitAll()
                );


        return http.build();
    }
}
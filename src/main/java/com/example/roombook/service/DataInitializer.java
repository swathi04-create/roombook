package com.example.roombook.service;

import com.example.roombook.model.User;
import com.example.roombook.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createDefaultUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create Admin
            if (userRepository.findByEmail("admin@roombook.com").isEmpty()) {

                User admin = new User();

                admin.setName("RoomBook Admin");
                admin.setEmail("admin@roombook.com");
                admin.setPassword(
                        passwordEncoder.encode("admin123")
                );
                admin.setRole("ADMIN");

                userRepository.save(admin);
            }

            // Create Customer
            if (userRepository.findByEmail("customer@roombook.com").isEmpty()) {

                User customer = new User();

                customer.setName("RoomBook Customer");
                customer.setEmail("customer@roombook.com");
                customer.setPassword(
                        passwordEncoder.encode("customer123")
                );
                customer.setRole("CUSTOMER");

                userRepository.save(customer);
            }
        };
    }
}
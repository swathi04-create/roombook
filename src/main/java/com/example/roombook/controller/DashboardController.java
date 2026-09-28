package com.example.roombook.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String dashboard(
            Authentication authentication,
            Model model) {

        String role = authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority()
                                .replace("ROLE_", ""))
                .orElse("");

        model.addAttribute(
                "role",
                role
        );

        model.addAttribute(
                "username",
                authentication.getName()
        );

        return "dashboard";
    }
}
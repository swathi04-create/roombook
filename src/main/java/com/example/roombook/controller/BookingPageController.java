package com.example.roombook.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookingPageController {

    @GetMapping("/bookings")
    public String bookingsPage(
            Authentication authentication,
            Model model) {

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority()
                                .replace("ROLE_", ""))
                .orElse("");

        model.addAttribute("role", role);

        return "bookings";
    }

    @GetMapping("/add-booking")
    public String addBookingPage(
            Authentication authentication,
            Model model) {

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority.getAuthority()
                                .replace("ROLE_", ""))
                .orElse("");

        model.addAttribute("role", role);

        return "add-booking";
    }
}
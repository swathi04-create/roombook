package com.example.roombook.controller;

import com.example.roombook.model.Booking;
import com.example.roombook.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Admin -> all bookings
    // Customer -> own bookings
    @GetMapping
    public List<Booking> getBookings(
            Authentication authentication) {

        if (isAdmin(authentication)) {
            return bookingService.getAllBookings();
        }

        return bookingService.getBookingsByUser(
                authentication.getName()
        );
    }


    // View single booking
    @GetMapping("/{id}")
    public Booking getBooking(
            @PathVariable Long id,
            Authentication authentication) {

        Booking booking =
                bookingService.getBookingById(id);

        checkAccess(booking, authentication);

        return booking;
    }


    // Create booking
    @PostMapping
    public Booking createBooking(
            @Valid @RequestBody Booking booking,
            Authentication authentication) {

        return bookingService.createBooking(
                booking,
                authentication.getName()
        );
    }


    // Update booking
    @PutMapping("/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody Booking booking,
            Authentication authentication) {

        Booking existing =
                bookingService.getBookingById(id);

        checkAccess(existing, authentication);

        return bookingService.updateBooking(
                id,
                booking
        );
    }


    // Cancel booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        Booking existing =
                bookingService.getBookingById(id);

        checkAccess(existing, authentication);

        bookingService.cancelBooking(id);

        return ResponseEntity.ok(
                "Booking cancelled successfully"
        );
    }


    // Check-in
    @PutMapping("/{id}/check-in")
    public Booking checkIn(
            @PathVariable Long id,
            Authentication authentication) {

        Booking existing =
                bookingService.getBookingById(id);

        checkAccess(existing, authentication);

        return bookingService.checkIn(id);
    }


    // Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(
            @PathVariable Long id,
            Authentication authentication) {

        Booking existing =
                bookingService.getBookingById(id);

        checkAccess(existing, authentication);

        bookingService.deleteBooking(id);

        return ResponseEntity.ok(
                "Booking deleted successfully"
        );
    }


    // Check whether logged-in user is Admin
    private boolean isAdmin(
            Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority()
                                .equals("ROLE_ADMIN"));
    }


    // Check booking ownership
    private void checkAccess(
            Booking booking,
            Authentication authentication) {

        // Admin can access everything
        if (isAdmin(authentication)) {
            return;
        }

        // Customer can access only their own booking
        if (booking.getUserEmail() == null ||
                !booking.getUserEmail()
                        .equals(authentication.getName())) {

            throw new RuntimeException(
                    "You are not allowed to access this booking"
            );
        }
    }
}
package com.example.roombook.service;

import com.example.roombook.model.Booking;
import com.example.roombook.model.Employee;
import com.example.roombook.model.Room;
import com.example.roombook.repository.BookingRepository;
import com.example.roombook.repository.EmployeeRepository;
import com.example.roombook.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final EmployeeRepository employeeRepository;

    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            EmployeeRepository employeeRepository) {

        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.employeeRepository = employeeRepository;
    }

    // Get all bookings - Admin use
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Get bookings of a particular user - Customer use
    public List<Booking> getBookingsByUser(String email) {
        return bookingRepository.findByUserEmail(email);
    }

    public Booking getBookingById(Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found"));
    }

    // Create booking
    public Booking createBooking(
            Booking booking,
            String userEmail) {

        if (booking.getRoom() == null
                || booking.getRoom().getId() == null) {

            throw new RuntimeException("Room is required");
        }

        if (booking.getEmployee() == null
                || booking.getEmployee().getId() == null) {

            throw new RuntimeException("Employee is required");
        }

        if (booking.getBookingDate() == null) {

            throw new RuntimeException("Booking date is required");
        }

        if (booking.getStartTime() == null
                || booking.getEndTime() == null) {

            throw new RuntimeException(
                    "Start time and end time are required");
        }

        if (!booking.getStartTime()
                .isBefore(booking.getEndTime())) {

            throw new RuntimeException(
                    "End time must be after start time");
        }

        Room room = roomRepository
                .findById(booking.getRoom().getId())
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        Employee employee = employeeRepository
                .findById(booking.getEmployee().getId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        // Check room conflict
        List<Booking> existingBookings =
                bookingRepository
                        .findByRoomAndBookingDateAndStatus(
                                room,
                                booking.getBookingDate(),
                                "CONFIRMED"
                        );

        for (Booking existing : existingBookings) {

            boolean overlaps =
                    booking.getStartTime()
                            .isBefore(existing.getEndTime())
                            &&
                            booking.getEndTime()
                                    .isAfter(existing.getStartTime());

            if (overlaps) {

                throw new RuntimeException(
                        "Room is already booked for the selected time"
                );
            }
        }

        booking.setRoom(room);
        booking.setEmployee(employee);

        // Logged-in user
        booking.setUserEmail(userEmail);

        booking.setStatus("CONFIRMED");
        booking.setCheckedIn(false);

        return bookingRepository.save(booking);
    }

    // Update booking
    public Booking updateBooking(
            Long id,
            Booking booking) {

        Booking existing = getBookingById(id);

        if (booking.getRoom() == null
                || booking.getRoom().getId() == null) {

            throw new RuntimeException("Room is required");
        }

        if (booking.getEmployee() == null
                || booking.getEmployee().getId() == null) {

            throw new RuntimeException("Employee is required");
        }

        if (booking.getBookingDate() == null) {

            throw new RuntimeException("Booking date is required");
        }

        if (booking.getStartTime() == null
                || booking.getEndTime() == null) {

            throw new RuntimeException(
                    "Start time and end time are required");
        }

        if (!booking.getStartTime()
                .isBefore(booking.getEndTime())) {

            throw new RuntimeException(
                    "End time must be after start time");
        }

        Room room = roomRepository
                .findById(booking.getRoom().getId())
                .orElseThrow(() ->
                        new RuntimeException("Room not found"));

        Employee employee = employeeRepository
                .findById(booking.getEmployee().getId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        List<Booking> existingBookings =
                bookingRepository
                        .findByRoomAndBookingDateAndStatus(
                                room,
                                booking.getBookingDate(),
                                "CONFIRMED"
                        );

        for (Booking other : existingBookings) {

            if (other.getId().equals(id)) {
                continue;
            }

            boolean overlaps =
                    booking.getStartTime()
                            .isBefore(other.getEndTime())
                            &&
                            booking.getEndTime()
                                    .isAfter(other.getStartTime());

            if (overlaps) {

                throw new RuntimeException(
                        "Room is already booked for the selected time"
                );
            }
        }

        existing.setRoom(room);
        existing.setEmployee(employee);
        existing.setBookingDate(
                booking.getBookingDate());
        existing.setStartTime(
                booking.getStartTime());
        existing.setEndTime(
                booking.getEndTime());
        existing.setPurpose(
                booking.getPurpose());

        return bookingRepository.save(existing);
    }

    // Cancel booking
    public void cancelBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus("CANCELLED");

        bookingRepository.save(booking);
    }

    // Delete booking
    public void deleteBooking(Long id) {

        if (!bookingRepository.existsById(id)) {

            throw new RuntimeException(
                    "Booking not found");
        }

        bookingRepository.deleteById(id);
    }

    // Check-in
    public Booking checkIn(Long id) {

        Booking booking = getBookingById(id);

        if (!"CONFIRMED".equalsIgnoreCase(
                booking.getStatus())) {

            throw new RuntimeException(
                    "Only confirmed bookings can be checked in");
        }

        booking.setCheckedIn(true);

        return bookingRepository.save(booking);
    }

    // Auto release after 10 minutes
    public void autoReleaseBookings() {

        LocalDate today = LocalDate.now();

        List<Booking> bookings =
                bookingRepository
                        .findByBookingDateAndStatus(
                                today,
                                "CONFIRMED"
                        );

        LocalDateTime now = LocalDateTime.now();

        for (Booking booking : bookings) {

            if (!booking.isCheckedIn()) {

                LocalDateTime releaseTime =
                        LocalDateTime.of(
                                booking.getBookingDate(),
                                booking.getStartTime()
                        ).plusMinutes(10);

                if (!now.isBefore(releaseTime)) {

                    booking.setStatus("RELEASED");

                    bookingRepository.save(booking);
                }
            }
        }
    }
}
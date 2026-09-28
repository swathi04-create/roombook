package com.example.roombook.repository;

import com.example.roombook.model.Booking;
import com.example.roombook.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomAndBookingDateAndStatus(
            Room room,
            LocalDate bookingDate,
            String status
    );

    List<Booking> findByBookingDateAndStatus(
            LocalDate bookingDate,
            String status
    );

    List<Booking> findByUserEmail(String userEmail);
}
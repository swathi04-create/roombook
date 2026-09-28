package com.example.roombook.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingScheduler {

    private final BookingService bookingService;

    public BookingScheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Scheduled(fixedRate = 60000)
    public void releaseExpiredBookings() {
        bookingService.autoReleaseBookings();
    }
}
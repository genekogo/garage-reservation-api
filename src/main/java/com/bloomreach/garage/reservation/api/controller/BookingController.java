package com.bloomreach.garage.reservation.api.controller;

import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/appointments/booking")
public class BookingController {

    private final BookingService service;

    @GetMapping
    public List<TimeSlot> findAvailableSlots(@RequestParam("date") LocalDate date,
                                             @RequestParam("operationId") Long operationId) {
        return service.findAvailableSlots(date, operationId);
    }
}

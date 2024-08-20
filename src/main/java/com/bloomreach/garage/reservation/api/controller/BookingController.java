package com.bloomreach.garage.reservation.api.controller;

import com.bloomreach.garage.reservation.api.entity.Appointment;
import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("availableSlots")
    public List<TimeSlot> findAvailableSlots(@RequestParam("date") LocalDate date,
                                             @RequestParam("operationId") Long operationId) {
        // TODO Add date, operationId
        // TODO Search by employeeId also? - optional
        return bookingService.findAvailableSlots(date, operationId);
    }

    @PostMapping("bookSlot")
    public ResponseEntity<Appointment> bookAppointment(@RequestBody BookingRequest request) {
        return new ResponseEntity<>(bookingService.bookAppointment(request), HttpStatus.CREATED);
    }
}

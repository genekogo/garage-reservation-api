package com.bloomreach.garage.reservation.api.controller;

import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing reservations and checking available time slots.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reservations")
@Tag(name = "Reservation", description = "APIs for making reservations and checking availability")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Gets available time slots for a specific date and list of operations.
     *
     * @param date         The date for which to check available time slots.
     * @param operationIds The list of operation IDs for which to check availability.
     * @return List of available time slots for the specified date and operations.
     */
    @GetMapping("/availableSlots")
    @Operation(summary = "Get available time slots", description = "Retrieves available time slots for a given date and list of operations.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available time slots")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(@RequestParam("date") LocalDate date,
                                                            @RequestParam("operationIds") List<Long> operationIds) {
        try {
            List<TimeSlot> availableSlots = reservationService.findAvailableSlots(date, operationIds);
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Books appointments based on the provided booking request.
     *
     * @param bookingRequest The booking request containing details for the appointment.
     * @return ResponseEntity indicating the result of the booking operation.
     */
    @PostMapping("/book")
    @Operation(summary = "Book appointments", description = "Books appointments based on the provided details.")
    @ApiResponse(responseCode = "200", description = "Successfully booked the appointment")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    public ResponseEntity<?> bookAppointments(@RequestBody BookingRequest bookingRequest) {
        try {
            reservationService.bookAppointments(bookingRequest);
            return ResponseEntity.ok("Appointment successfully booked");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Failed to book appointment: " + e.getMessage());
        }
    }
}

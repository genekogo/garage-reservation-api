package com.bloomreach.garage.reservation.api.controller;

import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.BookingResponse;
import com.bloomreach.garage.reservation.api.model.AvailabilityResponse;
import com.bloomreach.garage.reservation.api.service.AvailabilityService;
import com.bloomreach.garage.reservation.api.service.BookingService;
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

    private final AvailabilityService availabilityService;
    private final BookingService bookingService;

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
    public ResponseEntity<List<AvailabilityResponse>> getAvailableSlots(@RequestParam("date") LocalDate date,
                                                                        @RequestParam("operationIds") List<Long> operationIds) {
        try {
            List<AvailabilityResponse> availableSlots = availabilityService.findAvailableSlots(date, operationIds);
            return ResponseEntity.ok(availableSlots);
        } catch (Exception exc) {
            log.error("Error retrieving available slots: {}", exc.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Books appointments based on the provided booking request.
     *
     * @param bookingRequest The booking request containing details for the appointment.
     * @return ResponseEntity containing the booking details or error message.
     */
    @PostMapping("/book")
    @Operation(summary = "Book appointments", description = "Books appointments based on the provided details.")
    @ApiResponse(responseCode = "200", description = "Successfully booked the appointment")
    @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    public ResponseEntity<BookingResponse> bookAppointments(@RequestBody BookingRequest bookingRequest) {
        try {
            BookingResponse bookingResponse = bookingService.bookAppointment(bookingRequest);
            return ResponseEntity.ok(bookingResponse);
        } catch (Exception exc) {
            log.error("Error booking appointment: {}", exc.getMessage());
            return ResponseEntity.badRequest().body(null); // You can customize this response based on requirements
        }
    }
}

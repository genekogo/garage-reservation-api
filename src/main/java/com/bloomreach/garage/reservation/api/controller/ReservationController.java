package com.bloomreach.garage.reservation.api.controller;

import com.bloomreach.garage.reservation.api.error.ProcessingError;
import com.bloomreach.garage.reservation.api.error.ValidationError;
import com.bloomreach.garage.reservation.api.model.AvailableSlot;
import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.BookingResponse;
import com.bloomreach.garage.reservation.api.service.AvailabilityService;
import com.bloomreach.garage.reservation.api.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
     * Retrieves available time slots for the specified date and operation IDs.
     *
     * @param date         The date to check for available slots.
     * @param operationIds The list of operation IDs to check availability.
     * @return A Set of available time slots.
     */
    @GetMapping("/availableSlots")
    @Operation(summary = "Find available time slots", description = "Retrieves available time slots for the given date and operation IDs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved available time slots",
                    content = @Content(schema = @Schema(implementation = AvailableSlot.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "400", description = "Processing error",
                    content = @Content(schema = @Schema(implementation = ProcessingError.class)))
    })
    public List<AvailableSlot> findAvailableSlots(@RequestParam LocalDate date,
                                                  @RequestParam List<Long> operationIds) {
        return availabilityService.findAvailableSlots(date, operationIds);
    }

    /**
     * Books appointments based on the provided booking request.
     *
     * @param bookingRequest The booking request containing details for the appointment.
     * @return ResponseEntity containing the booking details or error message.
     */
    @PostMapping("/book")
    @Operation(summary = "Book appointments", description = "Books appointments based on the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved available time slots",
                    content = @Content(schema = @Schema(implementation = AvailableSlot.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ValidationError.class))),
            @ApiResponse(responseCode = "400", description = "Processing error",
                    content = @Content(schema = @Schema(implementation = ProcessingError.class)))
    })
    public BookingResponse bookAppointments(@RequestBody BookingRequest bookingRequest) {
        return bookingService.bookAppointment(bookingRequest);
    }
}

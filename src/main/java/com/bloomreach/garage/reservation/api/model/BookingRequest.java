package com.bloomreach.garage.reservation.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents a request to book an appointment.
 * <p>
 * This class captures the details required to create a booking, including the operations to be performed,
 * the customer making the booking, and the date and time of the appointment.
 * </p>
 */
@Data
@NoArgsConstructor
@Schema(description = "Request payload for booking an appointment.")
public class BookingRequest {

    @Schema(description = "List of operation IDs to be performed during the appointment.", example = "[1, 2, 3]")
    private List<Long> operationIds;

    @Schema(description = "ID of the customer making the booking.", example = "123")
    private Long customerId;

    @Schema(description = "Date of the appointment.", example = "2024-09-01")
    private LocalDate date;

    @Schema(description = "Start time of the appointment.", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "End time of the appointment.", example = "10:00:00")
    private LocalTime endTime;
}


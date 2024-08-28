package com.bloomreach.garage.reservation.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Represents a time slot with a start and end time.
 * <p>
 * This class is used to define a specific period of time during which an appointment or event can be scheduled.
 * It contains the start and end times of the slot, which are essential for determining availability for scheduling purposes.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a time slot with a start and end time.")
public class AvailableSlot {

    @Schema(description = "The start time of the time slot.",
            example = "09:00:00", pattern = "HH:mm:ss", type = "string")
    private LocalTime startTime;

    @Schema(description = "The end time of the time slot.",
            example = "10:00:00", pattern = "HH:mm:ss", type = "string")
    private LocalTime endTime;
}

package com.bloomreach.garage.reservation.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Represents a time slot with a start and end time.
 */
@Data
@NoArgsConstructor
@Schema(description = "Represents a time slot with a start and end time.")
public class TimeSlot {

    @Schema(description = "The start time of the time slot.")
    private LocalTime startTime;

    @Schema(description = "The end time of the time slot.")
    private LocalTime endTime;

    /**
     * Constructs a TimeSlot with the given start and end time.
     *
     * @param startTime The start time of the time slot.
     * @param endTime   The end time of the time slot.
     */
    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     *
     * @param other The other time slot to check against.
     * @return True if there is an overlap, false otherwise.
     */
    public boolean overlaps(TimeSlot other) {
        return this.startTime.isBefore(other.endTime)
                && other.startTime.isBefore(this.endTime);
    }
}

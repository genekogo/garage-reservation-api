package com.bloomreach.garage.reservation.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class TimeSlot {

    private LocalTime startTime;
    private LocalTime endTime;
}

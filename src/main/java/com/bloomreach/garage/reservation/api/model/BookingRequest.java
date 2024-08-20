package com.bloomreach.garage.reservation.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class BookingRequest {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long operationId;
    private Long customerId;
    // TODO Do I need employee id?
    private Long employeeId;
}


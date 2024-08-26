package com.bloomreach.garage.reservation.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
public class BookingRequest {

    private List<Long> operationIds;
    private Long customerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}


package com.bloomreach.garage.reservation.api.model;

import com.bloomreach.garage.reservation.api.entity.Customer;
import com.bloomreach.garage.reservation.api.entity.Employee;
import com.bloomreach.garage.reservation.api.entity.GarageBox;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class BookingResponse {

    private Customer customer;
    private GarageAppointment appointment;
    private List<GarageAppointmentOperation> operations;

    @Data
    @Builder
    public static class GarageAppointment {
        private Long id;
        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private GarageBox garageBox;
    }

    @Data
    @Builder
    public static class GarageAppointmentOperation {
        private Long id;
        private GarageOperation operation;
        private Employee employee;
    }
}

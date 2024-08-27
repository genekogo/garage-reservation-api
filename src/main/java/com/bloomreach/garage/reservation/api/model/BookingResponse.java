package com.bloomreach.garage.reservation.api.model;

import com.bloomreach.garage.reservation.api.entity.Customer;
import com.bloomreach.garage.reservation.api.entity.Employee;
import com.bloomreach.garage.reservation.api.entity.GarageBox;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents the response returned after a successful booking.
 * <p>
 * This class contains details about the customer, the booked appointment,
 * and the operations performed during the appointment.
 * </p>
 */
@Data
@Builder
@Schema(description = "Response payload containing details of the booking.")
public class BookingResponse {

    @Schema(description = "Details of the customer who made the booking.")
    private Customer customer;

    @Schema(description = "Details of the booked garage appointment.")
    private GarageAppointment appointment;

    @Schema(description = "List of operations performed during the appointment.")
    private List<GarageAppointmentOperation> operations;

    /**
     * Represents the details of a garage appointment.
     */
    @Data
    @Builder
    @Schema(description = "Details of a garage appointment.")
    public static class GarageAppointment {

        @Schema(description = "Unique identifier of the garage appointment.", example = "1")
        private Long id;

        @Schema(description = "Date of the appointment.", example = "2024-09-01")
        private LocalDate date;

        @Schema(description = "Start time of the appointment.", example = "09:00:00")
        private LocalTime startTime;

        @Schema(description = "End time of the appointment.", example = "10:00:00")
        private LocalTime endTime;

        @Schema(description = "Details of the garage box assigned for the appointment.")
        private GarageBox garageBox;
    }

    /**
     * Represents an operation performed during the garage appointment.
     */
    @Data
    @Builder
    @Schema(description = "Details of an operation performed during the garage appointment.")
    public static class GarageAppointmentOperation {

        @Schema(description = "Unique identifier of the operation.", example = "1")
        private Long id;

        @Schema(description = "Details of the operation performed.")
        private GarageOperation operation;

        @Schema(description = "Start time of the operation.", example = "09:00:00")
        private LocalTime startTime;

        @Schema(description = "End time of the operation.", example = "09:30:00")
        private LocalTime endTime;

        @Schema(description = "Details of the employee performing the operation.")
        private Employee employee;
    }
}

package com.bloomreach.garage.reservation.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "garage_appointment_operations")
@Schema(description = "Details of an operation associated with a garage appointment.")
public class GarageAppointmentOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the garage appointment operation", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    @NotNull
    @Schema(description = "The garage appointment associated with this operation")
    private GarageAppointment appointment;

    @ManyToOne
    @JoinColumn(name = "operation_id", nullable = false)
    @NotNull
    @Schema(description = "The operation being performed")
    private GarageOperation operation;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull
    @Schema(description = "The employee performing the operation")
    private Employee employee;

    @Column(name = "start_time", nullable = false)
    @NotNull
    @Schema(description = "Start time of the operation")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull
    @Schema(description = "End time of the operation")
    private LocalTime endTime;
}

package com.bloomreach.garage.reservation.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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
}

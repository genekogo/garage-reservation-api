package com.bloomreach.garage.reservation.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "garage_appointments")
@Schema(description = "Represents an appointment at the garage.")
public class GarageAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the appointment", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    @Schema(description = "The customer associated with the appointment")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "garage_box_id", nullable = false)
    @NotNull
    @Schema(description = "The garage box where the appointment will be held")
    private GarageBox garageBox;

    @Column(name = "date", nullable = false)
    @NotNull
    @Schema(description = "The date of the appointment", example = "2024-08-22")
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    @NotNull
    @Schema(description = "The start time of the appointment", example = "09:00:00")
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @NotNull
    @Schema(description = "The end time of the appointment", example = "17:00:00")
    private LocalTime endTime;

    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Operations associated with the appointment")
    private List<GarageAppointmentOperation> operations;
}

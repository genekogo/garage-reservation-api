package com.bloomreach.garage.reservation.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "garage_closures")
public class GarageClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private GarageClosureType closureType; // Type of closure

    @NotNull
    private LocalDate closureDate; // Date of the closure
}


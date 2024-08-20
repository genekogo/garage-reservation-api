package com.bloomreach.garage.reservation.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "garage_non_working_days")
public class GarageNonWorkingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GarageClosureType closureType;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDate date;
}

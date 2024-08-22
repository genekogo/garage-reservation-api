package com.bloomreach.garage.reservation.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "employees")
@Schema(description = "Employee entity representing a staff member in the garage")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the employee", example = "1")
    private Long id;

    @Schema(description = "Full name of the employee", example = "Mechanic A")
    private String fullName;

    @Schema(description = "Type of the employee", example = "Mechanic")
    private String employeeTypeId;
}

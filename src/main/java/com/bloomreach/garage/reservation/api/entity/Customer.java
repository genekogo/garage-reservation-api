package com.bloomreach.garage.reservation.api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customers")
@Schema(description = "Customer entity representing a client of the garage")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the customer", example = "1")
    private Long id;

    @Schema(description = "Full name of the customer", example = "John Doe")
    private String fullName;

    @Schema(description = "Phone number of the customer", example = "+31 6 12345678")
    private String phoneNumber;

    @Schema(description = "Email address of the customer", example = "john.doe@example.com")
    private String email; // Optional field
}

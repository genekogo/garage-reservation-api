package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for accessing and managing {@link Customer} entities.
 * <p>
 * This repository provides CRUD operations and custom query methods for the {@link Customer} entity.
 * It is exposed as a RESTful resource with the path "customers" for easy integration with REST clients.
 * </p>
 *
 * @see Customer
 */
@RepositoryRestResource(path = "customers")
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

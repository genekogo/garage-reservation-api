package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for accessing and managing {@link Employee} entities.
 * <p>
 * This repository provides CRUD operations and custom query methods for the {@link Employee} entity.
 * It is exposed as a RESTful resource with the path "employees" for easy integration with REST clients.
 * </p>
 *
 * @see Employee
 */
@RepositoryRestResource(path = "employees")
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

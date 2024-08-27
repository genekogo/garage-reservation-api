package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for accessing and managing {@link EmployeeType} entities.
 * <p>
 * This repository provides CRUD operations and custom query methods for the {@link EmployeeType} entity.
 * It is exposed as a RESTful resource with the path "employeeTypes" for easy integration with REST clients.
 * </p>
 *
 * @see EmployeeType
 */
@RepositoryRestResource(path = "employeeTypes")
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Long> {
}

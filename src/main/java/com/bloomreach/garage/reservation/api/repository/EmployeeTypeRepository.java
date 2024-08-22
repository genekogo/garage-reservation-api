package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "employeeTypes")
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Long> {
}

package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "employees")
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}

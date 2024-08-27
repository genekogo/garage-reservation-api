package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.DayOfWeek;
import java.util.List;

/**
 * Repository interface for accessing and managing {@link EmployeeWorkingHours} entities.
 * <p>
 * This repository provides CRUD operations and custom query methods for the {@link EmployeeWorkingHours} entity.
 * It is exposed as a RESTful resource with the path "employeeWorkingHours" for easy integration with REST clients.
 * </p>
 *
 * @see EmployeeWorkingHours
 */
@RepositoryRestResource(path = "employeeWorkingHours")
public interface EmployeeWorkingHoursRepository extends JpaRepository<EmployeeWorkingHours, Long> {

    /**
     * Finds working hours for employees on a specific day of the week.
     *
     * @param dayOfWeek The day of the week for which to find employee working hours (e.g., {@code DayOfWeek.MONDAY}).
     * @return A list of {@link EmployeeWorkingHours} for all employees on the specified day of the week.
     */
    List<EmployeeWorkingHours> findByDayOfWeek(DayOfWeek dayOfWeek);
}

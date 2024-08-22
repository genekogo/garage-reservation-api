package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "employeeWorkingHours")
public interface EmployeeWorkingHoursRepository extends JpaRepository<EmployeeWorkingHours, Long> {

    /**
     * Find working hours by employee and day of the week.
     *
     * @param employeeId The ID of the employee.
     * @param dayOfWeek  The day of the week (e.g., "Monday", "Tuesday").
     * @return Optional of working hours for the specified employee and day.
     */
    Optional<EmployeeWorkingHours> findByEmployeeIdAndDayOfWeek(Long employeeId, DayOfWeek dayOfWeek);

    /**
     * Find working hours by day of the week.
     *
     * @param dayOfWeek The day of the week (e.g., "Monday", "Tuesday").
     * @return List of working hours for all employees on the specified day.
     */
    List<EmployeeWorkingHours> findByDayOfWeek(DayOfWeek dayOfWeek);
}
package com.bloomreach.garage.reservation.api.component;

import com.bloomreach.garage.reservation.api.entity.Employee;
import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import com.bloomreach.garage.reservation.api.repository.EmployeeWorkingHoursRepository;
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Checks the availability of mechanics during a specified time slot.
 */
@RequiredArgsConstructor
@Component
public class MechanicAvailabilityChecker {

    private final EmployeeWorkingHoursRepository employeeWorkingHoursRepository;
    private final GarageAppointmentOperationRepository garageAppointmentOperationRepository;

    /**
     * Finds available mechanics who are not already assigned to other appointments during the specified time slot.
     *
     * @param date      The date of the appointment.
     * @param startTime The start time of the appointment.
     * @param endTime   The end time of the appointment.
     * @return A list of available mechanics who are not assigned to other appointments.
     */
    public List<Employee> findAvailableMechanics(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Fetch all working hours for the mechanics on the specified day of the week
        List<EmployeeWorkingHours> workingHoursList = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());

        // Create a map of employeeId to their working hours
        Map<Long, List<EmployeeWorkingHours>> employeeWorkingHoursMap = workingHoursList.stream()
                .collect(Collectors.groupingBy(workingHours -> workingHours.getEmployee().getId()));

        // Filter and find available mechanics based on working hours and appointment time slot
        return workingHoursList.stream()
                .map(EmployeeWorkingHours::getEmployee)
                .filter(employee -> {
                    // Get the working hours for the employee
                    List<EmployeeWorkingHours> employeeWorkingHours = employeeWorkingHoursMap.get(employee.getId());

                    // If no working hours are found for the employee, they are not available
                    if (employeeWorkingHours == null) {
                        return false;
                    }

                    // Check if the employee is available within the provided time slot
                    return employeeWorkingHours.stream()
                            .anyMatch(workingHours ->
                                    workingHours.getStartTime().isBefore(endTime) &&
                                            workingHours.getEndTime().isAfter(startTime) &&
                                            !hasOverlappingAppointments(employee.getId(), date, startTime, endTime));
                })
                .toList();
    }

    /**
     * Checks if the given employee has any overlapping appointments within the specified time slot.
     *
     * @param employeeId The ID of the employee.
     * @param date       The date of the appointment.
     * @param startTime  The start time of the appointment.
     * @param endTime    The end time of the appointment.
     * @return True if there are overlapping appointments, false otherwise.
     */
    private boolean hasOverlappingAppointments(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return !garageAppointmentOperationRepository
                .findOverlappingAppointments(employeeId, date, startTime, endTime)
                .isEmpty();
    }
}

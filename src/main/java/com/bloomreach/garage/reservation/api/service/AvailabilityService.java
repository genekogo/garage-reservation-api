package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.error.ErrorMessage;
import com.bloomreach.garage.reservation.api.error.ProcessingError;
import com.bloomreach.garage.reservation.api.error.ValidationError;
import com.bloomreach.garage.reservation.api.model.AvailabilityResponse;
import com.bloomreach.garage.reservation.api.repository.EmployeeWorkingHoursRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import com.bloomreach.garage.reservation.api.component.SlotCalculator;
import com.bloomreach.garage.reservation.api.validator.AvailabilityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for calculating and providing available time slots for garage operations
 * and checking mechanic availability.
 */
@RequiredArgsConstructor
@Service
public class AvailabilityService {

    private final GarageOperationRepository garageOperationRepository;
    private final EmployeeWorkingHoursRepository employeeWorkingHoursRepository;
    private final AvailabilityValidator availabilityValidator;
    private final SlotCalculator slotCalculator;

    /**
     * Finds available time slots for the specified date and list of operation IDs.
     *
     * @param date         The date for which to find available slots.
     * @param operationIds The list of operation IDs to check for availability.
     * @return A list of available time slots for the given date and operations.
     * @throws ValidationError if the date is not within the allowed range.
     */
    @Cacheable(value = "availableSlots", key = "#date.toString()")
    public List<AvailabilityResponse> findAvailableSlots(LocalDate date, List<Long> operationIds) {
        availabilityValidator.validate(date);

        List<GarageOperation> operations = garageOperationRepository.findAllById(operationIds);
        if (operations.size() != operationIds.size()) {
            throw new ValidationError(ErrorMessage.OPERATION_NOT_FOUND);
        }

        List<EmployeeWorkingHours> availableMechanics = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());
        List<AvailabilityResponse> availableSlots = new ArrayList<>();

        for (EmployeeWorkingHours workingHours : availableMechanics) {
            availableSlots.addAll(slotCalculator.calculateSlots(workingHours, operations));
        }

        return availableSlots;
    }

    /**
     * Checks if a mechanic is available during the specified time slot for the given date and operations.
     *
     * @param date         The date of the appointment.
     * @param startTime    The start time of the appointment slot.
     * @param endTime      The end time of the appointment slot.
     * @param operationIds The list of operation IDs to consider.
     * @return True if a mechanic is available, false otherwise.
     * @throws ProcessingError if any of the operation IDs are not found.
     */
    public boolean isMechanicAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, List<Long> operationIds) {
        List<GarageOperation> operations = garageOperationRepository.findAllById(operationIds);
        if (operations.size() != operationIds.size()) {
            throw new ProcessingError(ErrorMessage.OPERATION_NOT_FOUND);
        }

        List<EmployeeWorkingHours> availableMechanics = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());
        for (EmployeeWorkingHours workingHours : availableMechanics) {
            if (startTime.isBefore(workingHours.getEndTime()) && endTime.isAfter(workingHours.getStartTime())) {
                return true;
            }
        }

        return false;
    }
}

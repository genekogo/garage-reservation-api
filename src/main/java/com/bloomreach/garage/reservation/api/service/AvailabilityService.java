package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import com.bloomreach.garage.reservation.api.entity.GarageAppointmentOperation;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.repository.EmployeeWorkingHoursRepository;
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentOperationRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AvailabilityService {

    private final GarageOperationRepository garageOperationRepository;
    private final EmployeeWorkingHoursRepository employeeWorkingHoursRepository;
    private final GarageAppointmentOperationRepository garageAppointmentOperationRepository;

    /**
     * Finds available time slots for the specified date and operation IDs.
     *
     * @param date         The date for which to find available slots.
     * @param operationIds The list of operation IDs for which to find available slots.
     * @return A list of available time slots.
     */
    @Cacheable(value = "availableSlots", key = "#date.toString()")
    public List<TimeSlot> findAvailableSlots(LocalDate date, List<Long> operationIds) {
        validateDate(date);

        // Fetch operations based on IDs
        List<GarageOperation> operations = garageOperationRepository.findAllById(operationIds);
        if (operations.size() != operationIds.size()) {
            throw new IllegalArgumentException("One or more operations not found");
        }

        // Fetch working hours for the given date
        List<EmployeeWorkingHours> availableMechanics = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());

        // Calculate available slots based on mechanics' availability and operations' durations
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (EmployeeWorkingHours workingHours : availableMechanics) {
            availableSlots.addAll(calculateTimeSlotsForMechanic(workingHours, operations));
        }

        return availableSlots;
    }

    private void validateDate(LocalDate date) {
        LocalDate now = LocalDate.now();
        LocalDate twoWeeksFromNow = now.plusWeeks(2);

        if (date.isBefore(now)) {
            throw new IllegalArgumentException("Date cannot be in the past");
        }
        if (date.isAfter(twoWeeksFromNow)) {
            throw new IllegalArgumentException("Date cannot be more than 2 weeks in advance");
        }
    }

    private List<TimeSlot> calculateTimeSlotsForMechanic(EmployeeWorkingHours workingHours, List<GarageOperation> operations) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime start = workingHours.getStartTime();
        LocalTime end = workingHours.getEndTime();

        // Determine the minimum duration from operations
        int minDuration = operations.stream()
                .map(GarageOperation::getDurationInMinutes)
                .min(Integer::compareTo)
                .orElse(30); // Default to 30 minutes if no operations

        // Find available time slots
        while (start.plusMinutes(minDuration).isBefore(end)) {
            boolean canAccommodateAll = true;
            LocalTime slotEnd = start;

            for (GarageOperation operation : operations) {
                slotEnd = slotEnd.plusMinutes(operation.getDurationInMinutes());
                if (slotEnd.isAfter(end)) {
                    canAccommodateAll = false;
                    break;
                }
            }

            if (canAccommodateAll) {
                availableSlots.add(new TimeSlot(start, slotEnd));
            }

            start = start.plusMinutes(minDuration); // Move by the minimum duration
        }

        return availableSlots;
    }

    public boolean isMechanicAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, List<Long> operationIds) {
        List<GarageOperation> operations = garageOperationRepository.findAllById(operationIds);
        if (operations.size() != operationIds.size()) {
            throw new IllegalArgumentException("One or more operations not found");
        }

        // Check mechanic working hours
        List<EmployeeWorkingHours> availableMechanics = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());
        for (EmployeeWorkingHours workingHours : availableMechanics) {
            if (startTime.isBefore(workingHours.getEndTime()) && endTime.isAfter(workingHours.getStartTime())) {

                // Check for overlapping appointments
                List<GarageAppointmentOperation> overlappingAppointments = garageAppointmentOperationRepository.findOverlappingAppointments(
                        workingHours.getEmployee().getId(), date, startTime, endTime);

                if (overlappingAppointments.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }
}

package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.repository.EmployeeWorkingHoursRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import com.bloomreach.garage.reservation.config.ReservationProperties;
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
    private final ReservationProperties reservationProperties;

    /**
     * Finds available time slots for the specified date and list of operation IDs.
     * <p>
     * This method retrieves operations based on provided IDs and checks the available working hours
     * for mechanics on the given date. It then calculates possible time slots considering the duration
     * of each operation and mechanics' availability.
     *
     * @param date         The date for which to find available slots.
     * @param operationIds The list of operation IDs to check for availability.
     * @return A list of available time slots for the given date and operations.
     */
    @Cacheable(value = "availableSlots", key = "#date.toString()")
    public List<TimeSlot> findAvailableSlots(LocalDate date, List<Long> operationIds) {
        validateDate(date);  // Ensure the date is within the acceptable range

        // Fetch operations by their IDs
        List<GarageOperation> operations = garageOperationRepository.findAllById(operationIds);
        if (operations.size() != operationIds.size()) {
            throw new IllegalArgumentException("One or more operations not found");
        }

        // Retrieve working hours for mechanics on the specified date
        List<EmployeeWorkingHours> availableMechanics = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());

        // Calculate time slots based on mechanics' availability and operations' durations
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (EmployeeWorkingHours workingHours : availableMechanics) {
            availableSlots.addAll(calculateTimeSlotsForMechanic(workingHours, operations));
        }

        return availableSlots;
    }

    /**
     * Validates that the given date is within the allowable range (not in the past and within 2 weeks from now).
     *
     * @param date The date to validate.
     * @throws IllegalArgumentException if the date is not within the allowed range.
     */
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

    /**
     * Calculates available time slots for a mechanic based on their working hours and the given operations.
     * <p>
     * Time slots are determined by checking if each operation's duration fits within the mechanic's available working hours.
     * If no operations are available, a default slot duration is used to determine possible slots.
     *
     * @param workingHours The working hours of the mechanic.
     * @param operations   The list of operations to accommodate within the time slots.
     * @return A list of available time slots for the mechanic.
     */
    private List<TimeSlot> calculateTimeSlotsForMechanic(EmployeeWorkingHours workingHours, List<GarageOperation> operations) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime start = workingHours.getStartTime();
        LocalTime end = workingHours.getEndTime();

        // Determine the minimum operation duration or use the default duration if no operations are provided
        int minDuration = operations.stream()
                .map(GarageOperation::getDurationInMinutes)
                .min(Integer::compareTo)
                .orElse(reservationProperties.getDefaultSlotDuration()); // Use default slot duration if no operations found

        // Calculate possible time slots
        while (start.plusMinutes(minDuration).isBefore(end)) {
            boolean canAccommodateAll = true;
            LocalTime slotEnd = start;

            // Check if all operations fit within the time slot
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

            start = start.plusMinutes(minDuration); // Move to the next slot
        }

        return availableSlots;
    }

    /**
     * Checks if there is a mechanic available during the specified time slot for the given date and operations.
     * <p>
     * This method ensures that there are mechanics who are not only available but also not already assigned to other appointments.
     *
     * @param date         The date of the appointment.
     * @param startTime    The start time of the appointment slot.
     * @param endTime      The end time of the appointment slot.
     * @param operationIds The list of operation IDs to consider.
     * @return True if a mechanic is available, false otherwise.
     */
    public boolean isMechanicAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, List<Long> operationIds) {
        List<GarageOperation> operations = garageOperationRepository.findAllById(operationIds);
        if (operations.size() != operationIds.size()) {
            throw new IllegalArgumentException("One or more operations not found");
        }

        List<EmployeeWorkingHours> availableMechanics = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());
        for (EmployeeWorkingHours workingHours : availableMechanics) {
            if (startTime.isBefore(workingHours.getEndTime()) && endTime.isAfter(workingHours.getStartTime())) {
                return true; // Mechanic is available if the slot overlaps with their working hours
            }
        }

        return false; // No available mechanics for the specified time slot
    }
}

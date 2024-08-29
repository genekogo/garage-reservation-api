package com.bloomreach.garage.reservation.api.component;

import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.model.AvailableSlot;
import com.bloomreach.garage.reservation.config.ReservationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculates available time slots for mechanics based on their working hours
 * and the durations of the garage operations to be performed.
 */
@RequiredArgsConstructor
@Component
public class SlotCalculator {

    private final ReservationProperties reservationProperties;

    /**
     * Calculates available time slots for a given mechanic's working hours and a list of operations.
     *
     * @param workingHours The working hours of the mechanic.
     * @param operations   The list of operations to accommodate within the time slots.
     * @return A list of available time slots for the mechanic.
     */
    public List<AvailableSlot> calculateSlots(EmployeeWorkingHours workingHours, List<GarageOperation> operations) {
        List<AvailableSlot> availableSlots = new ArrayList<>();
        LocalTime start = workingHours.getStartTime();
        LocalTime end = workingHours.getEndTime();

        // Minimum slot duration
        int minDuration = reservationProperties.getDefaultSlotDuration();

        // Calculate minimum advance time
        LocalTime nowPlusMinAdvance = LocalTime.now().plusMinutes(reservationProperties.getMinAdvanceMinutes());

        // Calculate possible time slots
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

            if (canAccommodateAll && !start.isBefore(nowPlusMinAdvance)) {
                availableSlots.add(new AvailableSlot(start, slotEnd));
            }

            start = start.plusMinutes(minDuration);
        }

        return availableSlots;
    }
}

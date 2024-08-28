package com.bloomreach.garage.reservation.api.component;

import com.bloomreach.garage.reservation.api.entity.GarageBox;
import com.bloomreach.garage.reservation.api.error.ErrorMessage;
import com.bloomreach.garage.reservation.api.error.ProcessingError;
import com.bloomreach.garage.reservation.api.repository.GarageBoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Allocates an available garage box for an appointment.
 */
@RequiredArgsConstructor
@Component
public class GarageBoxAllocator {

    private final GarageBoxRepository garageBoxRepository;

    /**
     * Fetches an available garage box for the specified date and time slot.
     *
     * @param date      The date of the appointment.
     * @param startTime The start time of the appointment.
     * @param endTime   The end time of the appointment.
     * @return The allocated garage box.
     * @throws ProcessingError if no garage boxes are available.
     */
    public GarageBox allocateGarageBox(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Page<GarageBox> page = garageBoxRepository.findAvailableBox(
                date, startTime, endTime, PageRequest.of(0, 1));

        return page.getContent().stream()
                .findFirst()
                .orElseThrow(() -> new ProcessingError(ErrorMessage.NO_AVAILABLE_GARAGE_BOXES));
    }
}

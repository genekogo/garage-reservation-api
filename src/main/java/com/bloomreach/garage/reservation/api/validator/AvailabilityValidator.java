package com.bloomreach.garage.reservation.api.validator;

import com.bloomreach.garage.reservation.api.error.ErrorMessage;
import com.bloomreach.garage.reservation.api.error.ValidationError;
import com.bloomreach.garage.reservation.config.ReservationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Validates dates based on the reservation properties.
 * Ensures that the date is within a permissible range.
 */
@RequiredArgsConstructor
@Component
public class AvailabilityValidator {

    private final ReservationProperties reservationProperties;

    /**
     * Validates that the given date is within the allowable range
     * (not in the past and within the maximum number of advance days).
     *
     * @param date The date to validate.
     * @throws ValidationError if the date is not within the allowed range.
     */
    public void validate(LocalDate date) {
        LocalDate now = LocalDate.now();
        LocalDate maxDate = now.plusDays(reservationProperties.getMaxAdvanceDays());

        if (date.isBefore(now)) {
            throw new ValidationError(ErrorMessage.DATE_CANNOT_BE_IN_THE_PAST);
        }

        if (date.isAfter(maxDate)) {
            throw new ValidationError(String.format(
                    ErrorMessage.DATE_CANNOT_BE_MORE_THAN, reservationProperties.getMaxAdvanceDays()));
        }
    }
}

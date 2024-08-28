package com.bloomreach.garage.reservation.api.validator;

import com.bloomreach.garage.reservation.api.error.ErrorMessage;
import com.bloomreach.garage.reservation.api.error.ValidationError;
import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.config.ReservationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Validates booking requests based on the reservation properties.
 */
@RequiredArgsConstructor
@Component
public class BookingValidator {

    private final ReservationProperties reservationProperties;

    /**
     * Validates the booking request against the max-advance-days and min-advance-minutes constraints.
     *
     * @param request The booking request to validate.
     * @throws ValidationError if the request violates the defined constraints.
     */
    public void validate(BookingRequest request) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        if (request.getDate().isAfter(currentDate.plusDays(reservationProperties.getMaxAdvanceDays()))) {
            throw new ValidationError(String.format(ErrorMessage.BOOKING_CANNOT_BE_MADE_MORE_THAN,
                    reservationProperties.getMaxAdvanceDays()));
        }

        if (request.getDate().isEqual(currentDate)) {
            Duration durationBetweenNowAndBooking = Duration.between(currentTime, request.getStartTime());
            if (durationBetweenNowAndBooking.toMinutes() < reservationProperties.getMinAdvanceMinutes()) {
                throw new ValidationError(String.format(ErrorMessage.BOOKING_MUST_BE_MADE_AT_LEAST,
                        reservationProperties.getMinAdvanceMinutes()));
            }
        } else if (request.getDate().isBefore(currentDate)) {
            throw new ValidationError(ErrorMessage.BOOKING_CANNOT_BE_MADE_FOR_A_PAST_DATE);
        }
    }
}

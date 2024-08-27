package com.bloomreach.garage.reservation.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is a validation error in the request.
 * <p>
 * This exception is used to indicate that the input data provided by the user is invalid.
 * For example, it may be thrown when validation rules are violated, such as invalid input formats,
 * missing required fields, or data that does not meet the expected constraints.
 * </p>
 *
 * <p>
 * The HTTP status code for this exception is {@code 400 Bad Request}.
 * </p>
 *
 * @see HttpStatus#BAD_REQUEST
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationError extends RuntimeException {

    /**
     * Constructs a new {@code ValidationError} with the specified detail message.
     * <p>
     * The detail message provides additional information about the validation error,
     * which can be useful for debugging or informing the user about the nature of the error.
     * </p>
     *
     * @param message the detail message.
     */
    public ValidationError(String message) {
        super(message);
    }
}

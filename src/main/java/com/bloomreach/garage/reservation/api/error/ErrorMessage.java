package com.bloomreach.garage.reservation.api.error;

public final class ErrorMessage {

    public static final String OPERATION_ID_IS_REQUIRED = "Operation ID is required";
    public static final String OPERATION_NOT_FOUND = "One or more operations not found";
    public static final String DATE_CANNOT_BE_IN_THE_PAST = "Date cannot be in the past";
    public static final String DATE_CANNOT_BE_MORE_THAN = "Date cannot be more than %s days in advance";
    public static final String INVALID_CUSTOMER_ID = "Invalid customer ID";
    public static final String NO_AVAILABLE_GARAGE_BOXES = "No available garage boxes";
    public static final String NO_AVAILABLE_MECHANICS_FOR_THIS_TIME_SLOT = "No available mechanics for this time slot";
    public static final String NO_AVAILABLE_MECHANICS_FOR_THIS_OPERATION = "No available mechanics for this operation";
    public static final String BOOKING_CANNOT_BE_MADE_MORE_THAN = "Booking cannot be made more than %s days in advance.";
    public static final String BOOKING_MUST_BE_MADE_AT_LEAST = "Booking must be made at least %s minutes in advance.";
    public static final String BOOKING_CANNOT_BE_MADE_FOR_A_PAST_DATE = "Booking cannot be made for a past date.";

    private ErrorMessage() {
        // empty constructor
    }
}

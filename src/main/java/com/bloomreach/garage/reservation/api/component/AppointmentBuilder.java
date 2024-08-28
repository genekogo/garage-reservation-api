package com.bloomreach.garage.reservation.api.component;

import com.bloomreach.garage.reservation.api.entity.Customer;
import com.bloomreach.garage.reservation.api.entity.Employee;
import com.bloomreach.garage.reservation.api.entity.GarageAppointment;
import com.bloomreach.garage.reservation.api.entity.GarageAppointmentOperation;
import com.bloomreach.garage.reservation.api.entity.GarageBox;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.error.ErrorMessage;
import com.bloomreach.garage.reservation.api.error.ProcessingError;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Constructs a GarageAppointment and assigns mechanics to operations.
 */
@Component
public class AppointmentBuilder {

    /**
     * Creates a new appointment with the specified details and assigns mechanics to each operation.
     *
     * @param customer           The customer making the appointment.
     * @param date               The date of the appointment.
     * @param startTime          The start time of the appointment.
     * @param endTime            The end time of the appointment.
     * @param garageBox          The garage box allocated for the appointment.
     * @param operations         The list of operations to be performed.
     * @param availableMechanics The list of available mechanics.
     * @return The constructed GarageAppointment entity.
     */
    public GarageAppointment buildAppointment(Customer customer, LocalDate date,
                                              LocalTime startTime, LocalTime endTime,
                                              GarageBox garageBox, List<GarageOperation> operations,
                                              List<Employee> availableMechanics) {
        // Create a new appointment with the given details
        GarageAppointment appointment = GarageAppointment.builder()
                .customer(customer)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .garageBox(garageBox)
                .build();

        // Track the current time in the appointment
        AtomicReference<LocalTime> currentOperationStartTime = new AtomicReference<>(startTime);

        // Assign mechanics to each operation and calculate appointment operations
        List<GarageAppointmentOperation> appointmentOperations = operations.stream()
                .map(operation -> {
                    // Calculate the end time for this operation
                    Integer operationDuration = operation.getDurationInMinutes();
                    LocalTime operationEndTime = currentOperationStartTime.get().plusMinutes(operationDuration);

                    // Ensure at least one mechanic is available for this operation
                    if (availableMechanics.isEmpty()) {
                        throw new ProcessingError(ErrorMessage.NO_AVAILABLE_MECHANICS_FOR_THIS_OPERATION);
                    }

                    // Assign the first available mechanic to the operation
                    Employee assignedMechanic = availableMechanics.stream()
                            .findFirst()
                            .orElseThrow(() -> new ProcessingError(ErrorMessage.NO_AVAILABLE_MECHANICS_FOR_THIS_OPERATION));

                    // Create and return the appointment operation with the assigned mechanic
                    GarageAppointmentOperation appointmentOperation = GarageAppointmentOperation.builder()
                            .appointment(appointment)
                            .operation(operation)
                            .employee(assignedMechanic)
                            .startTime(currentOperationStartTime.get())
                            .endTime(operationEndTime)
                            .build();

                    // Update the start time for the next operation
                    currentOperationStartTime.set(operationEndTime);

                    return appointmentOperation;
                })
                .toList();

        // Set operations for the appointment
        appointment.setOperations(appointmentOperations);

        return appointment;
    }
}

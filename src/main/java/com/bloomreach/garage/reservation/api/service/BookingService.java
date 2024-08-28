package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.component.AppointmentBuilder;
import com.bloomreach.garage.reservation.api.component.GarageBoxAllocator;
import com.bloomreach.garage.reservation.api.component.MechanicAvailabilityChecker;
import com.bloomreach.garage.reservation.api.entity.Customer;
import com.bloomreach.garage.reservation.api.entity.Employee;
import com.bloomreach.garage.reservation.api.entity.GarageAppointment;
import com.bloomreach.garage.reservation.api.entity.GarageBox;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.error.ErrorMessage;
import com.bloomreach.garage.reservation.api.error.ProcessingError;
import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.BookingResponse;
import com.bloomreach.garage.reservation.api.repository.CustomerRepository;
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import com.bloomreach.garage.reservation.api.validator.BookingValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final AvailabilityService availabilityService;
    private final CustomerRepository customerRepository;
    private final GarageAppointmentRepository garageAppointmentRepository;
    private final GarageOperationRepository garageOperationRepository;
    private final GarageBoxAllocator garageBoxAllocator;
    private final MechanicAvailabilityChecker mechanicAvailabilityChecker;
    private final BookingValidator bookingValidator;
    private final AppointmentBuilder appointmentBuilder;

    /**
     * Books an appointment based on the provided booking request.
     *
     * @param request The booking request containing details of the appointment.
     * @return A response containing the booked appointment details.
     * @throws ProcessingError if validation fails or if resources are not available.
     */
    @Transactional
    @CacheEvict(value = "availableSlots", allEntries = true/*, condition = "#date != null" */)
    public BookingResponse bookAppointment(BookingRequest request) {
        // Validate the booking request
        bookingValidator.validate(request);

        // Validate that the slot is available using AvailabilityService
        boolean slotAvailable = availabilityService.isMechanicAvailable(
                request.getDate(), request.getStartTime(), request.getEndTime(), request.getOperationIds());
        if (!slotAvailable) {
            throw new ProcessingError(ErrorMessage.NO_AVAILABLE_MECHANICS_FOR_THIS_TIME_SLOT);
        }

        // Fetch the first available garage box
        GarageBox garageBox = garageBoxAllocator.allocateGarageBox(
                request.getDate(), request.getStartTime(), request.getEndTime());

        // Fetch the operations to be performed
        List<GarageOperation> operations = garageOperationRepository.findAllById(request.getOperationIds());
        if (operations.size() != request.getOperationIds().size()) {
            throw new ProcessingError(ErrorMessage.OPERATION_NOT_FOUND);
        }

        // Fetch the customer entity from the repository
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ProcessingError(ErrorMessage.INVALID_CUSTOMER_ID));

        // Find available mechanics for the operations
        List<Employee> availableMechanics = mechanicAvailabilityChecker.findAvailableMechanics(
                request.getDate(), request.getStartTime(), request.getEndTime());

        // Build the appointment with the given details
        GarageAppointment appointment = appointmentBuilder.buildAppointment(
                customer, request.getDate(), request.getStartTime(), request.getEndTime(), garageBox, operations, availableMechanics);

        // Save the appointment
        GarageAppointment savedAppointment = garageAppointmentRepository.save(appointment);

        // Build and return the response with the appointment and operation details
        return BookingResponse.builder()
                .customer(customer)
                .appointment(BookingResponse.GarageAppointment.builder()
                        .id(savedAppointment.getId())
                        .date(savedAppointment.getDate())
                        .startTime(savedAppointment.getStartTime())
                        .endTime(savedAppointment.getEndTime())
                        .garageBox(savedAppointment.getGarageBox())
                        .build())
                .operations(savedAppointment.getOperations().stream()
                        .map(operation -> BookingResponse.GarageAppointmentOperation.builder()
                                .id(operation.getId())
                                .operation(operation.getOperation())
                                .employee(operation.getEmployee())
                                .startTime(operation.getStartTime())
                                .endTime(operation.getEndTime())
                                .build())
                        .toList())
                .build();
    }
}

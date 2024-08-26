package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.entity.Customer;
import com.bloomreach.garage.reservation.api.entity.Employee;
import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import com.bloomreach.garage.reservation.api.entity.GarageAppointment;
import com.bloomreach.garage.reservation.api.entity.GarageAppointmentOperation;
import com.bloomreach.garage.reservation.api.entity.GarageBox;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.BookingResponse;
import com.bloomreach.garage.reservation.api.repository.CustomerRepository;
import com.bloomreach.garage.reservation.api.repository.EmployeeWorkingHoursRepository;
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentRepository;
import com.bloomreach.garage.reservation.api.repository.GarageBoxRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final AvailabilityService availabilityService;
    private final GarageAppointmentRepository garageAppointmentRepository;
    private final GarageOperationRepository garageOperationRepository;
    private final GarageBoxRepository garageBoxRepository;
    private final EmployeeWorkingHoursRepository employeeWorkingHoursRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    @CacheEvict(value = "availableSlots", key = "#request.date.toString() + '-' + #request.operationIds.toString()")
    public BookingResponse bookAppointment(BookingRequest request) {
        // Validate that the slot is available using AvailabilityService
        boolean slotAvailable = availabilityService.isMechanicAvailable(
                request.getDate(), request.getStartTime(), request.getEndTime(), request.getOperationIds());
        if (!slotAvailable) {
            throw new IllegalArgumentException("No available mechanics for this time slot");
        }

        // Fetch the first available garage box with limit 1
        Page<GarageBox> page = garageBoxRepository.findAvailableBox(
                request.getDate(), request.getStartTime(), request.getEndTime(), PageRequest.of(0, 1));

        GarageBox garageBox = page.getContent().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No available garage boxes"));

        // Fetch the operations to be performed
        List<GarageOperation> operations = garageOperationRepository.findAllById(request.getOperationIds());
        if (operations.size() != request.getOperationIds().size()) {
            throw new IllegalArgumentException("One or more operations not found");
        }

        // Fetch the customer entity
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        // Create the appointment
        GarageAppointment appointment = new GarageAppointment();
        appointment.setCustomer(customer);
        appointment.setDate(request.getDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setGarageBox(garageBox);

        // For each operation, assign it to the appointment and to an available mechanic
        List<GarageAppointmentOperation> appointmentOperations = operations.stream()
                .map(operation -> {
                    // Find available mechanics for the operation
                    List<EmployeeWorkingHours> availableMechanics = employeeWorkingHoursRepository.findByDayOfWeek(request.getDate().getDayOfWeek());

                    // Ensure unique selection of mechanics and operations
                    if (availableMechanics.isEmpty()) {
                        throw new IllegalArgumentException("No available mechanics for this operation");
                    }

                    // Filter mechanics who are available during the appointment time
                    Employee assignedMechanic = availableMechanics.stream()
                            .filter(workingHours ->
                                    (workingHours.getStartTime().isBefore(request.getEndTime()) &&
                                            workingHours.getEndTime().isAfter(request.getStartTime())))
                            .map(EmployeeWorkingHours::getEmployee)
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("No available mechanics for this operation"));

                    // Create and return the appointment operation
                    return GarageAppointmentOperation.builder()
                            .appointment(appointment)
                            .operation(operation)
                            .employee(assignedMechanic)
                            .build();
                })
                .collect(Collectors.toList());

        // Set operations for the appointment
        appointment.setOperations(appointmentOperations);

        // Save the appointment
        GarageAppointment savedAppointment = garageAppointmentRepository.save(appointment);

        // Create response
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
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

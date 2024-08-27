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
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentOperationRepository;
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentRepository;
import com.bloomreach.garage.reservation.api.repository.GarageBoxRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
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
    private final GarageAppointmentOperationRepository garageAppointmentOperationRepository;

    @Transactional
    @CacheEvict(value = "availableSlots", key = "#request.date.toString()")
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
        GarageAppointment appointment = GarageAppointment.builder()
                .customer(customer)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .garageBox(garageBox)
                .build();

        // Track the current time in the appointment
        AtomicReference<LocalTime> currentOperationStartTime = new AtomicReference<>(request.getStartTime());

        // Find and assign mechanics to operations
        List<GarageAppointmentOperation> appointmentOperations = operations.stream()
                .map(operation -> {
                    // Calculate the end time for this operation
                    Integer operationDuration = operation.getDurationInMinutes();
                    LocalTime operationEndTime = currentOperationStartTime.get().plusMinutes(operationDuration);

                    // Find available mechanics for the operation with its own time window
                    List<Employee> availableMechanics = findAvailableMechanics(request.getDate(), currentOperationStartTime.get(), operationEndTime);

                    // Ensure unique selection of mechanics and operations
                    if (availableMechanics.isEmpty()) {
                        throw new IllegalArgumentException("No available mechanics for this operation");
                    }

                    Employee assignedMechanic = availableMechanics.stream()
                            .findFirst()  // Pick the first available mechanic
                            .orElseThrow(() -> new IllegalArgumentException("No available mechanics for this operation"));

                    // Create and return the appointment operation with appointment set
                    GarageAppointmentOperation appointmentOperation = GarageAppointmentOperation.builder()
                            .appointment(appointment)
                            .operation(operation)
                            .employee(assignedMechanic)
                            .startTime(currentOperationStartTime.get())  // Add the start time
                            .endTime(operationEndTime)                    // Add the end time
                            .build();

                    // Update the currentOperationStartTime to the end time of the current operation
                    currentOperationStartTime.set(operationEndTime);

                    return appointmentOperation;
                })
                .toList();

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
                                .startTime(operation.getStartTime())
                                .endTime(operation.getEndTime())
                                .build())
                        .toList())
                .build();
    }

    /**
     * Finds available mechanics for the given date and time, excluding those already assigned.
     *
     * @param date      The date of the appointment.
     * @param startTime The start time of the appointment.
     * @param endTime   The end time of the appointment.
     * @return List of available mechanics.
     */
    private List<Employee> findAvailableMechanics(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Fetch all working hours for the day of the week
        List<EmployeeWorkingHours> workingHoursList = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());

        // Create a map of employeeId to their working hours
        Map<Long, List<EmployeeWorkingHours>> employeeWorkingHoursMap = workingHoursList.stream()
                .collect(Collectors.groupingBy(workingHours -> workingHours.getEmployee().getId()));

        // Find available mechanics based on appointment time, excluding those who are already assigned
        return workingHoursList.stream()
                .map(EmployeeWorkingHours::getEmployee)
                .filter(employee -> {
                    // Get the working hours for the employee
                    List<EmployeeWorkingHours> employeeWorkingHours = employeeWorkingHoursMap.get(employee.getId());

                    // If no working hours are found for the employee, they are not available
                    if (employeeWorkingHours == null) {
                        return false;
                    }

                    // Check if the employee is available in the given time slot
                    return employeeWorkingHours.stream()
                            .anyMatch(workingHours ->
                                    workingHours.getStartTime().isBefore(endTime) &&
                                            workingHours.getEndTime().isAfter(startTime) &&
                                            !hasOverlappingAppointments(employee.getId(), date, startTime, endTime));
                })
                .toList();
    }

    /**
     * Checks if an employee has overlapping appointments within the given time slot.
     *
     * @param employeeId The ID of the employee.
     * @param date       The date of the appointment.
     * @param startTime  The start time of the appointment.
     * @param endTime    The end time of the appointment.
     * @return True if there are overlapping appointments, false otherwise.
     */
    private boolean hasOverlappingAppointments(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<GarageAppointmentOperation> overlappingAppointments =
                garageAppointmentOperationRepository.findOverlappingAppointments(employeeId, date, startTime, endTime);
        return !overlappingAppointments.isEmpty();
    }
}

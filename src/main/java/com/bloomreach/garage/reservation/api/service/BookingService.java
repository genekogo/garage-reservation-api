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

    /**
     * Books an appointment based on the provided booking request.
     * <p>
     * This method performs the following steps:
     * 1. Checks if a mechanic is available for the requested time slot.
     * 2. Fetches an available garage box.
     * 3. Retrieves the requested operations.
     * 4. Creates and saves a new appointment with the selected garage box and assigned operations.
     * 5. Returns a response containing the appointment details.
     *
     * @param request The booking request containing details of the appointment.
     * @return A response containing the booked appointment details.
     * @throws IllegalArgumentException if no mechanics, garage boxes, or operations are available.
     */
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

        // Ensure there is at least one available garage box
        GarageBox garageBox = page.getContent().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No available garage boxes"));

        // Fetch the operations to be performed
        List<GarageOperation> operations = garageOperationRepository.findAllById(request.getOperationIds());
        if (operations.size() != request.getOperationIds().size()) {
            throw new IllegalArgumentException("One or more operations not found");
        }

        // Fetch the customer entity from the repository
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        // Create a new appointment with the given details
        GarageAppointment appointment = GarageAppointment.builder()
                .customer(customer)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .garageBox(garageBox)
                .build();

        // Track the current time in the appointment
        AtomicReference<LocalTime> currentOperationStartTime = new AtomicReference<>(request.getStartTime());

        // Assign mechanics to each operation and calculate appointment operations
        List<GarageAppointmentOperation> appointmentOperations = operations.stream()
                .map(operation -> {
                    // Calculate the end time for this operation
                    Integer operationDuration = operation.getDurationInMinutes();
                    LocalTime operationEndTime = currentOperationStartTime.get().plusMinutes(operationDuration);

                    // Find available mechanics for the operation within its time window
                    List<Employee> availableMechanics = findAvailableMechanics(request.getDate(), currentOperationStartTime.get(), operationEndTime);

                    // Ensure at least one mechanic is available for this operation
                    if (availableMechanics.isEmpty()) {
                        throw new IllegalArgumentException("No available mechanics for this operation");
                    }

                    // Assign the first available mechanic to the operation
                    Employee assignedMechanic = availableMechanics.stream()
                            .findFirst()  // Pick the first available mechanic
                            .orElseThrow(() -> new IllegalArgumentException("No available mechanics for this operation"));

                    // Create and return the appointment operation with the assigned mechanic
                    GarageAppointmentOperation appointmentOperation = GarageAppointmentOperation.builder()
                            .appointment(appointment)
                            .operation(operation)
                            .employee(assignedMechanic)
                            .startTime(currentOperationStartTime.get())  // Start time of the operation
                            .endTime(operationEndTime)                    // End time of the operation
                            .build();

                    // Update the start time for the next operation
                    currentOperationStartTime.set(operationEndTime);

                    return appointmentOperation;
                })
                .toList();

        // Set operations for the appointment and save it
        appointment.setOperations(appointmentOperations);
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

    /**
     * Finds available mechanics who are not already assigned to other appointments during the specified time slot.
     * <p>
     * This method retrieves all mechanics' working hours for the given day and filters them based on their availability
     * during the provided time slot.
     *
     * @param date      The date of the appointment.
     * @param startTime The start time of the appointment.
     * @param endTime   The end time of the appointment.
     * @return A list of available mechanics who are not assigned to other appointments.
     */
    private List<Employee> findAvailableMechanics(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Fetch all working hours for the mechanics on the specified day of the week
        List<EmployeeWorkingHours> workingHoursList = employeeWorkingHoursRepository.findByDayOfWeek(date.getDayOfWeek());

        // Create a map of employeeId to their working hours
        Map<Long, List<EmployeeWorkingHours>> employeeWorkingHoursMap = workingHoursList.stream()
                .collect(Collectors.groupingBy(workingHours -> workingHours.getEmployee().getId()));

        // Filter and find available mechanics based on working hours and appointment time slot
        return workingHoursList.stream()
                .map(EmployeeWorkingHours::getEmployee)
                .filter(employee -> {
                    // Get the working hours for the employee
                    List<EmployeeWorkingHours> employeeWorkingHours = employeeWorkingHoursMap.get(employee.getId());

                    // If no working hours are found for the employee, they are not available
                    if (employeeWorkingHours == null) {
                        return false;
                    }

                    // Check if the employee is available within the provided time slot
                    return employeeWorkingHours.stream()
                            .anyMatch(workingHours ->
                                    workingHours.getStartTime().isBefore(endTime) &&
                                            workingHours.getEndTime().isAfter(startTime) &&
                                            !hasOverlappingAppointments(employee.getId(), date, startTime, endTime));
                })
                .toList();
    }

    /**
     * Checks if the given employee has any overlapping appointments within the specified time slot.
     * <p>
     * This method queries the repository for any existing appointments that overlap with the given time slot
     * for the specified employee.
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

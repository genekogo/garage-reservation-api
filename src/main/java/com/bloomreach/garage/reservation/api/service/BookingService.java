package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.entity.*;
import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final AppointmentRepository appointmentRepository;
    private final GarageWorkingHoursRepository workingHoursRepository;
    private final GarageOperationRepository operationRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    public List<TimeSlot> findAvailableSlots(LocalDate date, Long operationId) {
        // Validate input parameters
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (operationId == null || operationId <= 0) {
            throw new IllegalArgumentException("Invalid operation ID");
        }

        // Date validation: Ensure the date is not in the past and not more than one month in advance
        LocalDate currentDate = LocalDate.now();
        LocalDate oneMonthFromNow = currentDate.plusMonths(1);

        if (date.isBefore(currentDate)) {
            throw new IllegalArgumentException("Date cannot be in the past");
        }
        if (date.isAfter(oneMonthFromNow)) {
            throw new IllegalArgumentException("Date cannot be more than one month in advance");
        }

        List<TimeSlot> availableSlots = new ArrayList<>();

        // Fetch garage working hours for the given date
        int dayOfWeek = date.getDayOfWeek().getValue();  // Convert to numeric day of week
        GarageWorkingHours workingHours = workingHoursRepository.findByDayOfWeek(dayOfWeek);

        if (workingHours == null) {
            return availableSlots; // No working hours available for this day
        }

        // Convert opening and closing times to LocalTime
        LocalTime openingTime = workingHours.getOpeningTime();
        LocalTime closingTime = workingHours.getClosingTime();

        // Fetch existing appointments for the given date
        List<Appointment> existingAppointments = appointmentRepository.findByAppointmentDate(date);

        // Get the duration for the specified operation by ID in minutes
        int operationDurationInMinutes = getOperationDuration(operationId);
        if (operationDurationInMinutes == -1) {
            throw new IllegalArgumentException("Invalid operation ID");
        }

        // Generate available slots
        availableSlots = generateAvailableSlots(openingTime, closingTime, existingAppointments, operationDurationInMinutes, date);

        return availableSlots;
    }

    @Transactional
    public Appointment bookAppointment(BookingRequest request) {
        // Validate booking request
        if (request == null || request.getOperationId() == null ||
                request.getEmployeeId() == null || request.getCustomerId() == null ||
                request.getStartTime() == null || request.getEndTime() == null ||
                request.getDate() == null) {
            throw new IllegalArgumentException("Invalid booking request");
        }

        // Validate the appointment date
        LocalDate appointmentDate = request.getDate();
        LocalTime startTime = request.getStartTime();
        LocalTime endTime = request.getEndTime();

        LocalDate currentDate = LocalDate.now();
        LocalDate oneMonthFromNow = currentDate.plusMonths(1);

        if (appointmentDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("Appointment date cannot be in the past");
        }
        if (appointmentDate.isAfter(oneMonthFromNow)) {
            throw new IllegalArgumentException("Appointment date cannot be more than one month in advance");
        }
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Invalid time slot");
        }

        // Fetch the operation, employee, and customer
        GarageOperation operation = operationRepository.findById(request.getOperationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid operation ID"));
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        // Check if the time slot is within working hours
        int dayOfWeek = appointmentDate.getDayOfWeek().getValue();
        GarageWorkingHours workingHours = workingHoursRepository.findByDayOfWeek(dayOfWeek);

        if (workingHours == null || startTime.isBefore(workingHours.getOpeningTime()) ||
                endTime.isAfter(workingHours.getClosingTime())) {
            throw new IllegalArgumentException("Requested time slot is outside of working hours");
        }

        // Check if the time slot conflicts with existing appointments
        List<Appointment> existingAppointments = appointmentRepository.findByAppointmentDate(appointmentDate);
        for (Appointment appointment : existingAppointments) {
            if ((startTime.isBefore(appointment.getEndTime()) && endTime.isAfter(appointment.getStartTime())) ||
                    (appointment.getStartTime().isBefore(endTime) && appointment.getEndTime().isAfter(startTime))) {
                throw new IllegalArgumentException("Time slot is already booked");
            }
        }

        // Create and save the new appointment
        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setOperation(operation);
        appointment.setCustomer(customer);
        appointment.setEmployee(employee);

        return appointmentRepository.save(appointment);
    }

    private int getOperationDuration(Long operationId) {
        // Fetch the operation details from the repository using ID
        GarageOperation operation = operationRepository.findById(operationId).orElse(null);
        if (operation == null) {
            return -1; // Indicates invalid operation
        }
        return operation.getDurationInMinutes();
    }

    private List<TimeSlot> generateAvailableSlots(LocalTime openingTime, LocalTime closingTime,
                                                  List<Appointment> existingAppointments, int operationDurationInMinutes, LocalDate date) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime startTime = openingTime;

        // Sort appointments by start time for efficient overlap checking
        existingAppointments.sort(Comparator.comparing(Appointment::getStartTime));

        // Get current time for today
        LocalTime currentTime = LocalTime.now();

        while (startTime.plusMinutes(operationDurationInMinutes).isBefore(closingTime) ||
                startTime.plusMinutes(operationDurationInMinutes).equals(closingTime)) {
            boolean isSlotAvailable = true;
            LocalTime slotEndTime = startTime.plusMinutes(operationDurationInMinutes);

            // Check if the current slot overlaps with any existing appointment
            for (Appointment appointment : existingAppointments) {
                if ((startTime.isBefore(appointment.getEndTime()) && slotEndTime.isAfter(appointment.getStartTime())) ||
                        (appointment.getStartTime().isBefore(slotEndTime) && appointment.getEndTime().isAfter(startTime))) {
                    isSlotAvailable = false;
                    startTime = appointment.getEndTime(); // Skip to the end time of the conflicting appointment
                    break;
                }
            }

            // Exclude slots before current time if the date is today
            if (date.equals(LocalDate.now()) && startTime.isBefore(currentTime)) {
                isSlotAvailable = false;
            }

            if (isSlotAvailable) {
                availableSlots.add(new TimeSlot(startTime, slotEndTime));
                startTime = startTime.plusMinutes(operationDurationInMinutes); // Move to the next slot
            } else {
                // Skip to the next potential start time after the conflict
                startTime = startTime.plusMinutes(operationDurationInMinutes);
            }
        }

        return availableSlots; // Returns an empty list if no slots are available
    }
}

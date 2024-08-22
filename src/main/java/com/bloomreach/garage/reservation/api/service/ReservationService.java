package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.entity.EmployeeWorkingHours;
import com.bloomreach.garage.reservation.api.entity.GarageAppointment;
import com.bloomreach.garage.reservation.api.entity.GarageAppointmentOperation;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.model.BookingRequest;
import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.repository.EmployeeWorkingHoursRepository;
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentOperationRepository;
import com.bloomreach.garage.reservation.api.repository.GarageAppointmentRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final GarageAppointmentRepository garageAppointmentRepository;
    private final GarageOperationRepository garageOperationRepository;
    private final EmployeeWorkingHoursRepository employeeWorkingHoursRepository;
    private final GarageAppointmentOperationRepository garageAppointmentOperationRepository;

    /**
     * Finds available slots for a given date and list of operations.
     *
     * @param date         The date for which available slots are to be found.
     * @param operationIds The list of operation IDs to check availability for.
     * @return List of available time slots.
     */
    public List<TimeSlot> findAvailableSlots(LocalDate date, List<Long> operationIds) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<EmployeeWorkingHours> workingHours = employeeWorkingHoursRepository.findByDayOfWeek(dayOfWeek);

        // Fetch existing appointments for the given date
        List<GarageAppointment> existingAppointments = garageAppointmentRepository.findByDate(date);

        // Create a map of existing appointment slots for easy lookup
        Map<Long, List<TimeSlot>> busySlotsMap = new HashMap<>();
        for (GarageAppointment appointment : existingAppointments) {
            for (GarageAppointmentOperation operation : appointment.getOperations()) {
                if (operationIds.contains(operation.getOperation().getId())) {
                    TimeSlot busySlot = new TimeSlot(appointment.getStartTime(), appointment.getEndTime());
                    busySlotsMap.computeIfAbsent(operation.getOperation().getId(), k -> new ArrayList<>())
                            .add(busySlot);
                }
            }
        }

        // Generate available slots for each employee based on working hours and busy slots
        List<TimeSlot> availableSlots = new ArrayList<>();
        for (EmployeeWorkingHours hours : workingHours) {
            LocalTime startTime = hours.getStartTime();
            LocalTime endTime = hours.getEndTime();
            while (startTime.isBefore(endTime)) {
                TimeSlot slot = new TimeSlot(startTime, startTime.plusHours(1));
                boolean isAvailable = true;
                for (Long operationId : operationIds) {
                    List<TimeSlot> busySlots = busySlotsMap.getOrDefault(operationId, Collections.emptyList());
                    for (TimeSlot busySlot : busySlots) {
                        if (slot.overlaps(busySlot)) {
                            isAvailable = false;
                            break;
                        }
                    }
                    if (!isAvailable) break;
                }
                if (isAvailable) {
                    availableSlots.add(slot);
                }
                startTime = startTime.plusHours(1);
            }
        }
        return availableSlots;
    }

    /**
     * Books appointments based on the provided request.
     *
     * @param request The booking request containing details for the appointment.
     * @return The booked garage appointment.
     */
    public GarageAppointment bookAppointments(BookingRequest request) {
        // Validate operations
        List<GarageOperation> operations = garageOperationRepository.findAllById(request.getOperationIds());
        if (operations.size() != request.getOperationIds().size()) {
            throw new IllegalArgumentException("One or more operations are invalid");
        }

        // Create a new appointment
        GarageAppointment appointment = new GarageAppointment();
        appointment.setDate(request.getDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());

        // Save the appointment to get the generated ID
        GarageAppointment savedAppointment = garageAppointmentRepository.save(appointment);

        // Link operations to the appointment
        List<GarageAppointmentOperation> appointmentOperations = new ArrayList<>();
        for (GarageOperation operation : operations) {
            GarageAppointmentOperation appointmentOperation = new GarageAppointmentOperation();
            appointmentOperation.setAppointment(savedAppointment);
            appointmentOperation.setOperation(operation);
            appointmentOperation.setEmployee(null);  // Set employee later based on availability
            appointmentOperations.add(appointmentOperation);
        }
        garageAppointmentOperationRepository.saveAll(appointmentOperations);

        return savedAppointment;
    }
}

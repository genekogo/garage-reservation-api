package com.bloomreach.garage.reservation.api.service;

import com.bloomreach.garage.reservation.api.entity.Appointment;
import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import com.bloomreach.garage.reservation.api.entity.GarageWorkingHours;
import com.bloomreach.garage.reservation.api.model.TimeSlot;
import com.bloomreach.garage.reservation.api.repository.AppointmentRepository;
import com.bloomreach.garage.reservation.api.repository.GarageOperationRepository;
import com.bloomreach.garage.reservation.api.repository.GarageWorkingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final GarageWorkingHoursRepository workingHoursRepository;
    private final AppointmentRepository appointmentRepository;
    private final GarageOperationRepository operationRepository;

    public List<TimeSlot> findAvailableSlots(LocalDate date, Long operationId) {
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

        LocalTime startTime = openingTime;

        // Calculate available slots
        while (startTime.plusMinutes(operationDurationInMinutes).isBefore(closingTime) || startTime.plusMinutes(operationDurationInMinutes).equals(closingTime)) {
            boolean isSlotAvailable = true;
            LocalTime slotEndTime = startTime.plusMinutes(operationDurationInMinutes);

            for (Appointment appointment : existingAppointments) {
                if ((startTime.isBefore(appointment.getEndTime()) && slotEndTime.isAfter(appointment.getStartTime())) ||
                        (appointment.getStartTime().isBefore(slotEndTime) && appointment.getEndTime().isAfter(startTime))) {
                    isSlotAvailable = false;
                    break;
                }
            }

            if (isSlotAvailable) {
                availableSlots.add(new TimeSlot(startTime, slotEndTime));
            }

            // Increment by the operation duration to avoid overlapping with slots of the same duration
            startTime = startTime.plusMinutes(operationDurationInMinutes);
        }

        return availableSlots;
    }

    private int getOperationDuration(Long operationId) {
        // Fetch the operation details from the repository using ID
        GarageOperation operation = operationRepository.findById(operationId).orElse(null);
        if (operation == null) {
            return -1; // Indicates invalid operation
        }
        return operation.getDurationInMinutes();
    }
}

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
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final GarageWorkingHoursRepository workingHoursRepository;
    private final AppointmentRepository appointmentRepository;
    private final GarageOperationRepository operationRepository;

    public List<TimeSlot> findAvailableSlots(LocalDate date, Long operationId) {
        // Validate input parameters
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (operationId == null || operationId <= 0) {
            throw new IllegalArgumentException("Invalid operation ID");
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
            throw new IllegalArgumentException("Invalid operation id");
        }

        // Generate available slots
        availableSlots = generateAvailableSlots(openingTime, closingTime, existingAppointments, operationDurationInMinutes);

        return availableSlots;
    }

    private int getOperationDuration(Long operationId) {
        // Fetch the operation details from the repository using ID
        return operationRepository.findById(operationId)
                .map(GarageOperation::getDurationInMinutes)
                .orElse(-1);
    }

    private List<TimeSlot> generateAvailableSlots(LocalTime openingTime, LocalTime closingTime,
                                                  List<Appointment> existingAppointments, int operationDurationInMinutes) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime startTime = openingTime;

        // Sort appointments by start time for efficient overlap checking
        existingAppointments.sort(Comparator.comparing(Appointment::getStartTime));

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

            if (isSlotAvailable) {
                availableSlots.add(new TimeSlot(startTime, slotEndTime));
                startTime = startTime.plusMinutes(operationDurationInMinutes); // Move to the next slot
            }
        }

        return availableSlots;
    }
}

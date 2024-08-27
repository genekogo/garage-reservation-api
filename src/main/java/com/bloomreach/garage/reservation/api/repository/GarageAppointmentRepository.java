package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RepositoryRestResource(path = "garageAppointments")
public interface GarageAppointmentRepository extends JpaRepository<GarageAppointment, Long> {

    List<GarageAppointment> findByDateAndStartTimeBeforeAndEndTimeAfter(LocalDate date, LocalTime startTime, LocalTime endTime);
}

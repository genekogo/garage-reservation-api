package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageAppointmentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RepositoryRestResource(path = "garageAppointmentOperations")
public interface GarageAppointmentOperationRepository extends JpaRepository<GarageAppointmentOperation, Long> {

    @Query("""
            SELECT gao FROM GarageAppointmentOperation gao WHERE gao.employee.id = :employeeId
            AND gao.appointment.date = :date
            AND ((:startTime < gao.endTime AND :endTime > gao.startTime))
            """)
    List<GarageAppointmentOperation> findOverlappingAppointments(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime);
}

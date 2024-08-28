package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageAppointmentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository interface for accessing and managing {@link GarageAppointmentOperation} entities.
 * <p>
 * This repository provides standard CRUD operations for {@link GarageAppointmentOperation} entities
 * and custom query methods for finding operations based on specific criteria.
 * It is exposed as a RESTful resource with the path "garageAppointmentOperations" for easy integration with REST clients.
 * </p>
 *
 * @see GarageAppointmentOperation
 */
@RepositoryRestResource(path = "garageAppointmentOperations")
public interface GarageAppointmentOperationRepository extends JpaRepository<GarageAppointmentOperation, Long> {

    /**
     * Finds all garage appointment operations for a specific employee on a given date
     * that overlap with the provided time window.
     * <p>
     * An operation overlaps if it has any time period within the given start and end times,
     * including cases where the operation starts before and ends after the specified time window.
     * </p>
     *
     * @param employeeId The ID of the employee whose operations are to be checked.
     * @param date       The date of the appointment operations to search for.
     * @param startTime  The start time of the time window to check for overlaps.
     * @param endTime    The end time of the time window to check for overlaps.
     * @return A list of {@link GarageAppointmentOperation} entities that overlap with the specified time window.
     */
    @Query("""
            SELECT gao FROM GarageAppointmentOperation gao WHERE gao.employee.id = :employeeId
            AND gao.appointment.date = :date
            AND ((:startTime < gao.endTime AND :endTime > gao.startTime))
            """)
    List<GarageAppointmentOperation> findOverlappingAppointments(Long employeeId, LocalDate date, LocalTime startTime, LocalTime endTime);
}

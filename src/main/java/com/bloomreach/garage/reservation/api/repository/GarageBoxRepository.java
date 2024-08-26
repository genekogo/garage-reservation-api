package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDate;
import java.time.LocalTime;

@RepositoryRestResource(path = "garageBoxes")
public interface GarageBoxRepository extends JpaRepository<GarageBox, Long>, QueryByExampleExecutor<GarageBox> {

    /**
     * Find an available garage box based on the requested date and time.
     *
     * @param date      The date of the appointment.
     * @param startTime The start time of the appointment.
     * @param endTime   The end time of the appointment.
     * @return Page of GarageBox that is available.
     */
    @Query("""
            SELECT gb FROM GarageBox gb
            WHERE NOT EXISTS (
                SELECT 1 FROM GarageAppointment ga
                WHERE ga.garageBox = gb
                AND ga.date = :date
                AND ((ga.startTime < :endTime AND ga.endTime > :startTime))
            )
            """)
    Page<GarageBox> findAvailableBox(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            Pageable pageable
    );
}

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

/**
 * Repository interface for managing {@link GarageBox} entities.
 * <p>
 * This repository provides methods to query and manipulate garage box data,
 * including finding available garage boxes for given time slots.
 * </p>
 */
@RepositoryRestResource(path = "garageBoxes")
public interface GarageBoxRepository extends JpaRepository<GarageBox, Long>, QueryByExampleExecutor<GarageBox> {

    /**
     * Finds garage boxes that are available for booking on a specific date and time range.
     * <p>
     * The method checks if the garage box is not already reserved for the specified date
     * and time range by querying for overlaps with existing appointments. Only those garage
     * boxes that do not have any overlapping appointments will be included in the result.
     * </p>
     *
     * @param date      The date on which the garage box is needed.
     * @param startTime The start time of the desired appointment slot.
     * @param endTime   The end time of the desired appointment slot.
     * @param pageable  The pagination information to control the number of results returned
     *                  and the page of results.
     * @return A {@link Page} of {@link GarageBox} entities that are available for the specified
     * date and time slot. The results are paginated based on the provided {@code pageable}.
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

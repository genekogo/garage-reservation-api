package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for accessing {@link GarageAppointment} entities.
 * <p>
 * This interface provides CRUD operations for {@link GarageAppointment} entities using
 * Spring Data JPA. It extends {@link JpaRepository}, which includes basic operations such
 * as saving, finding, deleting, and querying {@link GarageAppointment} entities.
 * </p>
 * <p>
 * The repository is exposed through the REST API at the path "/garageAppointments" for
 * managing appointments in the garage.
 * </p>
 */
@RepositoryRestResource(path = "garageAppointments")
public interface GarageAppointmentRepository extends JpaRepository<GarageAppointment, Long> {
}

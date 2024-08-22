package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageAppointmentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "garageAppointmentOperations")
public interface GarageAppointmentOperationRepository extends JpaRepository<GarageAppointmentOperation, Long> {
}

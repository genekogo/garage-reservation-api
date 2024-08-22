package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "garageOperations")
public interface GarageOperationRepository extends JpaRepository<GarageOperation, Long> {
}

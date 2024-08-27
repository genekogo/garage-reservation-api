package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for managing {@link GarageOperation} entities.
 * <p>
 * This repository provides standard CRUD operations for {@link GarageOperation} entities
 * through Spring Data JPA. It extends {@link JpaRepository}, which offers methods to save,
 * find, delete, and query {@link GarageOperation} instances.
 * </p>
 * <p>
 * The repository is exposed via the REST API at the path "/garageOperations", enabling
 * access and manipulation of garage operation records through HTTP requests.
 * </p>
 */
@RepositoryRestResource(path = "garageOperations")
public interface GarageOperationRepository extends JpaRepository<GarageOperation, Long> {
}

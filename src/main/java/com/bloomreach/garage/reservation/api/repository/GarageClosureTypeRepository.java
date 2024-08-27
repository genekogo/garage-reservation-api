package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageClosureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for managing {@link GarageClosureType} entities.
 * <p>
 * This repository provides standard CRUD operations for {@link GarageClosureType} entities
 * through Spring Data JPA. It extends {@link JpaRepository}, which offers methods to save,
 * find, delete, and query {@link GarageClosureType} instances.
 * </p>
 * <p>
 * The repository is exposed via the REST API at the path "/garageClosureTypes", enabling
 * access and manipulation of garage closure type records through HTTP requests.
 * </p>
 */
@RepositoryRestResource(path = "garageClosureTypes")
public interface GarageClosureTypeRepository extends JpaRepository<GarageClosureType, Long> {
}

package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Repository interface for accessing {@link GarageClosure} entities.
 * <p>
 * This repository provides standard CRUD operations for {@link GarageClosure} entities
 * via Spring Data JPA. It extends {@link JpaRepository}, offering methods to save, find,
 * delete, and query {@link GarageClosure} instances.
 * </p>
 * <p>
 * The repository is exposed through the REST API at the path "/garageClosures", allowing
 * for management and querying of garage closure records through HTTP requests.
 * </p>
 */
@RepositoryRestResource(path = "garageClosures")
public interface GarageClosureRepository extends JpaRepository<GarageClosure, Long> {
}

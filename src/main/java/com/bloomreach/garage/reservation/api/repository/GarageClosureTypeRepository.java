package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageClosureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "garageClosureTypes")
public interface GarageClosureTypeRepository extends JpaRepository<GarageClosureType, Long> {
}

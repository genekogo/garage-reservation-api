package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "garageBoxes")
public interface GarageBoxRepository extends JpaRepository<GarageBox, Long> {
}

package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.MechanicTimeOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MechanicTimeOffRepository extends JpaRepository<MechanicTimeOff, Long> {
}

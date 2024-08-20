package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.GarageWorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GarageWorkingHoursRepository extends JpaRepository<GarageWorkingHours, Long> {

    GarageWorkingHours findByDayOfWeek(int dayOfWeek);
}

package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.RecurrencePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrencePatternRepository extends JpaRepository<RecurrencePattern, Long> {
}

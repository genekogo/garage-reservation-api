package com.bloomreach.garage.reservation.api.repository;

import com.bloomreach.garage.reservation.api.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "customers")
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}

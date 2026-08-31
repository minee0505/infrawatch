package com.infrawatch.facility.repository;

import com.infrawatch.facility.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
}

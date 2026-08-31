package com.infrawatch.sensor.repository;

import com.infrawatch.sensor.domain.Sensor;
import com.infrawatch.sensor.domain.SensorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    List<Sensor> findByFacilityId(Long facilityId);

    List<Sensor> findByStatus(SensorStatus status);

    long countByFacilityId(Long facilityId);

    long countByStatus(SensorStatus status);
}

package com.infrawatch.reading.repository;

import com.infrawatch.reading.domain.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    Optional<SensorReading> findFirstBySensorIdOrderByMeasuredAtDesc(Long sensorId);

    List<SensorReading> findBySensorIdAndMeasuredAtBetweenOrderByMeasuredAtAsc(
            Long sensorId, LocalDateTime from, LocalDateTime to);

    List<SensorReading> findTop20BySensorIdOrderByMeasuredAtDesc(Long sensorId);
}

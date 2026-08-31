package com.infrawatch.alert.repository;

import com.infrawatch.alert.domain.Alert;
import com.infrawatch.alert.domain.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status);

    List<Alert> findAllByOrderByCreatedAtDesc();

    List<Alert> findTop10ByOrderByCreatedAtDesc();

    long countByStatus(AlertStatus status);

    Optional<Alert> findFirstBySensorIdAndStatusOrderByCreatedAtDesc(Long sensorId, AlertStatus status);

    @Query("select count(a) from Alert a where a.sensor.facility.id = :facilityId and a.status = :status")
    long countByFacilityIdAndStatus(@Param("facilityId") Long facilityId, @Param("status") AlertStatus status);
}

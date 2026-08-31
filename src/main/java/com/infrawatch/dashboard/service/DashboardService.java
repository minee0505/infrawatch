package com.infrawatch.dashboard.service;

import com.infrawatch.alert.domain.AlertLevel;
import com.infrawatch.alert.domain.AlertStatus;
import com.infrawatch.alert.dto.AlertResponse;
import com.infrawatch.alert.repository.AlertRepository;
import com.infrawatch.dashboard.dto.DashboardSummaryResponse;
import com.infrawatch.facility.repository.FacilityRepository;
import com.infrawatch.sensor.domain.SensorStatus;
import com.infrawatch.sensor.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final FacilityRepository facilityRepository;
    private final SensorRepository sensorRepository;
    private final AlertRepository alertRepository;

    public DashboardSummaryResponse getSummary() {
        long criticalOpenCount = alertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.OPEN).stream()
                .filter(alert -> alert.getLevel() == AlertLevel.CRITICAL)
                .count();

        return new DashboardSummaryResponse(
                facilityRepository.count(),
                sensorRepository.count(),
                sensorRepository.countByStatus(SensorStatus.ACTIVE),
                alertRepository.countByStatus(AlertStatus.OPEN),
                criticalOpenCount,
                alertRepository.findTop10ByOrderByCreatedAtDesc().stream().map(AlertResponse::from).toList());
    }
}

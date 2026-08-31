package com.infrawatch.dashboard.dto;

import com.infrawatch.alert.dto.AlertResponse;

import java.util.List;

public record DashboardSummaryResponse(
        long facilityCount,
        long sensorCount,
        long activeSensorCount,
        long openAlertCount,
        long criticalAlertCount,
        List<AlertResponse> recentAlerts
) {
}

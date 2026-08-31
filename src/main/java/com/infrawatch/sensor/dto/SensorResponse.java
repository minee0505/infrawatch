package com.infrawatch.sensor.dto;

import com.infrawatch.sensor.domain.Sensor;

import java.time.LocalDateTime;

public record SensorResponse(
        Long id,
        Long facilityId,
        String facilityName,
        String type,
        String name,
        String unit,
        Double thresholdMin,
        Double thresholdMax,
        String status,
        Double latestValue,
        LocalDateTime latestMeasuredAt
) {

    public static SensorResponse of(Sensor sensor, Double latestValue, LocalDateTime latestMeasuredAt) {
        return new SensorResponse(
                sensor.getId(),
                sensor.getFacility().getId(),
                sensor.getFacility().getName(),
                sensor.getType().name(),
                sensor.getName(),
                sensor.getUnit(),
                sensor.getThresholdMin(),
                sensor.getThresholdMax(),
                sensor.getStatus().name(),
                latestValue,
                latestMeasuredAt);
    }
}

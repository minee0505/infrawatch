package com.infrawatch.alert.dto;

import com.infrawatch.alert.domain.Alert;

import java.time.LocalDateTime;

public record AlertResponse(
        Long id,
        Long sensorId,
        String sensorName,
        Long facilityId,
        String facilityName,
        String level,
        String message,
        String status,
        Double readingValue,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        String resolvedByName
) {

    public static AlertResponse from(Alert alert) {
        return new AlertResponse(
                alert.getId(),
                alert.getSensor().getId(),
                alert.getSensor().getName(),
                alert.getSensor().getFacility().getId(),
                alert.getSensor().getFacility().getName(),
                alert.getLevel().name(),
                alert.getMessage(),
                alert.getStatus().name(),
                alert.getReading() != null ? alert.getReading().getValue() : null,
                alert.getCreatedAt(),
                alert.getResolvedAt(),
                alert.getResolvedBy() != null ? alert.getResolvedBy().getName() : null);
    }
}

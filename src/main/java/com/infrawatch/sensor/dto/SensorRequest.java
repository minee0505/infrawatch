package com.infrawatch.sensor.dto;

import com.infrawatch.sensor.domain.SensorStatus;
import com.infrawatch.sensor.domain.SensorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SensorRequest(
        @NotNull SensorType type,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 20) String unit,
        Double thresholdMin,
        Double thresholdMax,
        SensorStatus status
) {
}

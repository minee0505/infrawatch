package com.infrawatch.reading.dto;

import com.infrawatch.reading.domain.SensorReading;

import java.time.LocalDateTime;

public record ReadingResponse(
        Long id,
        Long sensorId,
        Double value,
        LocalDateTime measuredAt
) {

    public static ReadingResponse from(SensorReading reading) {
        return new ReadingResponse(
                reading.getId(), reading.getSensor().getId(), reading.getValue(), reading.getMeasuredAt());
    }
}

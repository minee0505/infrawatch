package com.infrawatch.reading.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReadingCreateRequest(
        @NotNull Double value,
        /** 비워두면 서버가 수신 시각을 측정 시각으로 쓴다. */
        LocalDateTime measuredAt
) {
}

package com.infrawatch.reading.controller;

import com.infrawatch.reading.dto.ReadingCreateRequest;
import com.infrawatch.reading.dto.ReadingResponse;
import com.infrawatch.reading.service.ReadingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sensors/{sensorId}/readings")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;

    @GetMapping
    public List<ReadingResponse> getReadings(
            @PathVariable Long sensorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        if (from == null || to == null) {
            return readingService.getRecent(sensorId);
        }
        return readingService.getHistory(sensorId, from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReadingResponse record(@PathVariable Long sensorId, @Valid @RequestBody ReadingCreateRequest request) {
        return readingService.record(sensorId, request);
    }
}

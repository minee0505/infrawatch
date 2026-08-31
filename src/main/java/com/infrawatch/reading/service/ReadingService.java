package com.infrawatch.reading.service;

import com.infrawatch.alert.service.AlertService;
import com.infrawatch.global.error.BusinessException;
import com.infrawatch.global.error.ErrorCode;
import com.infrawatch.reading.domain.SensorReading;
import com.infrawatch.reading.dto.ReadingCreateRequest;
import com.infrawatch.reading.dto.ReadingResponse;
import com.infrawatch.reading.repository.SensorReadingRepository;
import com.infrawatch.sensor.domain.Sensor;
import com.infrawatch.sensor.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadingService {

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final AlertService alertService;

    public List<ReadingResponse> getHistory(Long sensorId, LocalDateTime from, LocalDateTime to) {
        findSensor(sensorId);
        return sensorReadingRepository
                .findBySensorIdAndMeasuredAtBetweenOrderByMeasuredAtAsc(sensorId, from, to).stream()
                .map(ReadingResponse::from)
                .toList();
    }

    public List<ReadingResponse> getRecent(Long sensorId) {
        findSensor(sensorId);
        return sensorReadingRepository.findTop20BySensorIdOrderByMeasuredAtDesc(sensorId).stream()
                .map(ReadingResponse::from)
                .toList();
    }

    @Transactional
    public ReadingResponse record(Long sensorId, ReadingCreateRequest request) {
        Sensor sensor = findSensor(sensorId);
        SensorReading reading = record(sensor, request.value(),
                request.measuredAt() != null ? request.measuredAt() : LocalDateTime.now());
        return ReadingResponse.from(reading);
    }

    /** 수동 등록과 시뮬레이터가 같은 경로로 저장·알림 평가를 하도록 공통 로직을 뺐다. */
    @Transactional
    public SensorReading record(Sensor sensor, double value, LocalDateTime measuredAt) {
        if (!sensor.isActive()) {
            throw new BusinessException(ErrorCode.SENSOR_INACTIVE);
        }
        SensorReading reading = SensorReading.create(sensor, value, measuredAt);
        sensorReadingRepository.save(reading);
        alertService.raiseIfBreached(sensor, reading);
        return reading;
    }

    private Sensor findSensor(Long sensorId) {
        return sensorRepository.findById(sensorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SENSOR_NOT_FOUND));
    }
}

package com.infrawatch.sensor.service;

import com.infrawatch.audit.service.AuditLogService;
import com.infrawatch.facility.domain.Facility;
import com.infrawatch.facility.repository.FacilityRepository;
import com.infrawatch.global.error.BusinessException;
import com.infrawatch.global.error.ErrorCode;
import com.infrawatch.reading.domain.SensorReading;
import com.infrawatch.reading.repository.SensorReadingRepository;
import com.infrawatch.sensor.domain.Sensor;
import com.infrawatch.sensor.domain.SensorStatus;
import com.infrawatch.sensor.dto.SensorRequest;
import com.infrawatch.sensor.dto.SensorResponse;
import com.infrawatch.sensor.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SensorService {

    private final SensorRepository sensorRepository;
    private final FacilityRepository facilityRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final AuditLogService auditLogService;

    public List<SensorResponse> getSensors(Long facilityId) {
        return sensorRepository.findByFacilityId(facilityId).stream().map(this::toResponse).toList();
    }

    public SensorResponse getSensor(Long sensorId) {
        return toResponse(findSensor(sensorId));
    }

    @Transactional
    public SensorResponse create(Long facilityId, SensorRequest request, Long actorId) {
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));

        Sensor sensor = Sensor.create(
                facility, request.type(), request.name(), request.unit(),
                request.thresholdMin(), request.thresholdMax());
        sensorRepository.save(sensor);

        auditLogService.record(actorId, "SENSOR_CREATED", "SENSOR", String.valueOf(sensor.getId()), sensor.getName());
        return toResponse(sensor);
    }

    @Transactional
    public SensorResponse update(Long sensorId, SensorRequest request, Long actorId) {
        Sensor sensor = findSensor(sensorId);
        SensorStatus status = request.status() != null ? request.status() : sensor.getStatus();
        sensor.update(request.name(), request.unit(), request.thresholdMin(), request.thresholdMax(), status);

        auditLogService.record(actorId, "SENSOR_UPDATED", "SENSOR", String.valueOf(sensorId), sensor.getName());
        return toResponse(sensor);
    }

    @Transactional
    public void delete(Long sensorId, Long actorId) {
        Sensor sensor = findSensor(sensorId);
        sensorRepository.delete(sensor);
        auditLogService.record(actorId, "SENSOR_DELETED", "SENSOR", String.valueOf(sensorId), sensor.getName());
    }

    private Sensor findSensor(Long sensorId) {
        return sensorRepository.findById(sensorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SENSOR_NOT_FOUND));
    }

    private SensorResponse toResponse(Sensor sensor) {
        return sensorReadingRepository.findFirstBySensorIdOrderByMeasuredAtDesc(sensor.getId())
                .map(reading -> SensorResponse.of(sensor, reading.getValue(), reading.getMeasuredAt()))
                .orElseGet(() -> SensorResponse.of(sensor, null, null));
    }
}

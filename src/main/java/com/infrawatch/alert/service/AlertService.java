package com.infrawatch.alert.service;

import com.infrawatch.alert.domain.Alert;
import com.infrawatch.alert.domain.AlertLevel;
import com.infrawatch.alert.domain.AlertStatus;
import com.infrawatch.alert.dto.AlertResponse;
import com.infrawatch.alert.repository.AlertRepository;
import com.infrawatch.audit.service.AuditLogService;
import com.infrawatch.global.error.BusinessException;
import com.infrawatch.global.error.ErrorCode;
import com.infrawatch.member.domain.Member;
import com.infrawatch.member.repository.MemberRepository;
import com.infrawatch.reading.domain.SensorReading;
import com.infrawatch.sensor.domain.Sensor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    /** 임계치를 이 비율 이상 벗어나면 경고가 아니라 위험 단계로 올린다. */
    private static final double CRITICAL_RATIO = 1.2;

    private final AlertRepository alertRepository;
    private final MemberRepository memberRepository;
    private final AuditLogService auditLogService;

    public List<AlertResponse> getAlerts(AlertStatus status) {
        List<Alert> alerts = status != null
                ? alertRepository.findByStatusOrderByCreatedAtDesc(status)
                : alertRepository.findAllByOrderByCreatedAtDesc();
        return alerts.stream().map(AlertResponse::from).toList();
    }

    /**
     * 측정값이 임계치를 벗어났는지 확인하고, 벗어났다면 알림을 만든다.
     * 같은 센서에 이미 열려 있는 알림이 있으면 반복해서 만들지 않는다.
     */
    @Transactional
    public void raiseIfBreached(Sensor sensor, SensorReading reading) {
        double value = reading.getValue();
        if (!sensor.breaches(value)) {
            return;
        }
        if (alertRepository.findFirstBySensorIdAndStatusOrderByCreatedAtDesc(sensor.getId(), AlertStatus.OPEN).isPresent()) {
            return;
        }

        AlertLevel level = isSeverelyOutOfRange(sensor, value) ? AlertLevel.CRITICAL : AlertLevel.WARNING;
        String message = buildMessage(sensor, value);

        alertRepository.save(Alert.raise(sensor, reading, level, message));
        auditLogService.record(null, "ALERT_RAISED", "ALERT", null,
                sensor.getName() + " " + message);
    }

    @Transactional
    public AlertResponse acknowledge(Long alertId, Long actorId) {
        Alert alert = findAlert(alertId);
        alert.acknowledge();
        auditLogService.record(actorId, "ALERT_ACKNOWLEDGED", "ALERT", String.valueOf(alertId), alert.getMessage());
        return AlertResponse.from(alert);
    }

    @Transactional
    public AlertResponse resolve(Long alertId, Long actorId) {
        Alert alert = findAlert(alertId);
        if (alert.isResolved()) {
            throw new BusinessException(ErrorCode.ALERT_ALREADY_RESOLVED);
        }
        Member actor = memberRepository.findById(actorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        alert.resolve(actor);
        auditLogService.record(actorId, "ALERT_RESOLVED", "ALERT", String.valueOf(alertId), alert.getMessage());
        return AlertResponse.from(alert);
    }

    private boolean isSeverelyOutOfRange(Sensor sensor, double value) {
        if (sensor.getThresholdMax() != null && value > sensor.getThresholdMax()) {
            return value > sensor.getThresholdMax() * CRITICAL_RATIO;
        }
        if (sensor.getThresholdMin() != null && value < sensor.getThresholdMin()) {
            return sensor.getThresholdMin() == 0 || value < sensor.getThresholdMin() / CRITICAL_RATIO;
        }
        return false;
    }

    private String buildMessage(Sensor sensor, double value) {
        if (sensor.getThresholdMax() != null && value > sensor.getThresholdMax()) {
            return "측정값 %.2f%s (이)가 상한 %.2f%s 를 초과했습니다.".formatted(
                    value, sensor.getUnit(), sensor.getThresholdMax(), sensor.getUnit());
        }
        return "측정값 %.2f%s (이)가 하한 %.2f%s 미만입니다.".formatted(
                value, sensor.getUnit(), sensor.getThresholdMin(), sensor.getUnit());
    }

    private Alert findAlert(Long alertId) {
        return alertRepository.findById(alertId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ALERT_NOT_FOUND));
    }
}

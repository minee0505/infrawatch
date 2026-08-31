package com.infrawatch.reading.scheduler;

import com.infrawatch.reading.repository.SensorReadingRepository;
import com.infrawatch.reading.service.ReadingService;
import com.infrawatch.sensor.domain.Sensor;
import com.infrawatch.sensor.domain.SensorStatus;
import com.infrawatch.sensor.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 실제 IoT 장비가 없는 데모 환경에서 센서 값을 흉내 낸다.
 * 정상 범위 안에서 작은 잡음으로 움직이다가, 낮은 확률로 임계치를 벗어나
 * 알림 파이프라인(측정값 등록 -> 임계치 판단 -> 알림 생성)이 실제로 동작하는 것을 보여준다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.simulator", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SensorDataSimulator {

    /** 정상 범위를 벗어난 값을 만들어낼 확률 */
    private static final double SPIKE_PROBABILITY = 0.08;

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final ReadingService readingService;

    @Transactional
    @Scheduled(fixedRateString = "${app.simulator.interval-ms:20000}")
    public void generate() {
        List<Sensor> activeSensors = sensorRepository.findByStatus(SensorStatus.ACTIVE);
        for (Sensor sensor : activeSensors) {
            double next = nextValue(sensor);
            readingService.record(sensor, next, LocalDateTime.now());
        }
        if (!activeSensors.isEmpty()) {
            log.debug("센서 {}개에 대한 모의 측정값을 생성했습니다.", activeSensors.size());
        }
    }

    private double nextValue(Sensor sensor) {
        double baseline = baseline(sensor);
        double range = normalRange(sensor);
        double noise = (Math.random() - 0.5) * range * 0.1;
        double value = baseline + noise;

        if (Math.random() < SPIKE_PROBABILITY) {
            value = spike(sensor, range);
        }
        return Math.round(value * 100.0) / 100.0;
    }

    private double baseline(Sensor sensor) {
        return sensorReadingRepository.findFirstBySensorIdOrderByMeasuredAtDesc(sensor.getId())
                .map(reading -> reading.getValue())
                .orElseGet(() -> midpoint(sensor));
    }

    private double midpoint(Sensor sensor) {
        Double min = sensor.getThresholdMin();
        Double max = sensor.getThresholdMax();
        if (min != null && max != null) {
            return (min + max) / 2;
        }
        if (max != null) {
            return max * 0.6;
        }
        if (min != null) {
            return min * 1.4 + 1;
        }
        return 50.0;
    }

    private double normalRange(Sensor sensor) {
        Double min = sensor.getThresholdMin();
        Double max = sensor.getThresholdMax();
        if (min != null && max != null) {
            return Math.max(max - min, 1.0);
        }
        return Math.max(midpoint(sensor), 1.0);
    }

    private double spike(Sensor sensor, double range) {
        Double max = sensor.getThresholdMax();
        Double min = sensor.getThresholdMin();
        boolean pushHigh = max != null && (min == null || Math.random() < 0.5);
        if (pushHigh) {
            return max + range * (0.1 + Math.random() * 0.3);
        }
        return min - range * (0.1 + Math.random() * 0.3);
    }
}

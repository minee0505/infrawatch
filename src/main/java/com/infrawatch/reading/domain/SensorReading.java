package com.infrawatch.reading.domain;

import com.infrawatch.sensor.domain.Sensor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;

import java.time.LocalDateTime;

/**
 * 센서 측정값. 수정되지 않는 이력 데이터라 BaseTimeEntity 대신 생성 시각만 남긴다.
 */
@Getter
@Entity
@Table(name = "sensor_reading")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(name = "value", nullable = false)
    private Double value;

    /** 센서가 실제로 측정한 시각. 수집 시각(createdAt)과 다를 수 있다. */
    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private SensorReading(Sensor sensor, Double value, LocalDateTime measuredAt) {
        this.sensor = sensor;
        this.value = value;
        this.measuredAt = measuredAt;
    }

    public static SensorReading create(Sensor sensor, Double value, LocalDateTime measuredAt) {
        return new SensorReading(sensor, value, measuredAt);
    }
}

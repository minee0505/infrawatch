package com.infrawatch.alert.domain;

import com.infrawatch.global.entity.BaseTimeEntity;
import com.infrawatch.member.domain.Member;
import com.infrawatch.reading.domain.SensorReading;
import com.infrawatch.sensor.domain.Sensor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "alert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_id")
    private SensorReading reading;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    private AlertLevel level;

    @Column(name = "message", nullable = false, length = 300)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AlertStatus status;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by")
    private Member resolvedBy;

    private Alert(Sensor sensor, SensorReading reading, AlertLevel level, String message) {
        this.sensor = sensor;
        this.reading = reading;
        this.level = level;
        this.message = message;
        this.status = AlertStatus.OPEN;
    }

    public static Alert raise(Sensor sensor, SensorReading reading, AlertLevel level, String message) {
        return new Alert(sensor, reading, level, message);
    }

    public void acknowledge() {
        if (status != AlertStatus.OPEN) {
            return;
        }
        this.status = AlertStatus.ACKNOWLEDGED;
    }

    public void resolve(Member member) {
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = member;
    }

    public boolean isResolved() {
        return status == AlertStatus.RESOLVED;
    }
}

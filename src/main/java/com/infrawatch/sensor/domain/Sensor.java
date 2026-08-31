package com.infrawatch.sensor.domain;

import com.infrawatch.facility.domain.Facility;
import com.infrawatch.global.entity.BaseTimeEntity;
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

@Getter
@Entity
@Table(name = "sensor")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sensor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private SensorType type;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    /** 정상 범위의 하한과 상한. 방향에 따라 하나만 쓸 수도 있어 둘 다 nullable 이다. */
    @Column(name = "threshold_min")
    private Double thresholdMin;

    @Column(name = "threshold_max")
    private Double thresholdMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SensorStatus status;

    private Sensor(Facility facility, SensorType type, String name, String unit,
                    Double thresholdMin, Double thresholdMax) {
        this.facility = facility;
        this.type = type;
        this.name = name;
        this.unit = unit;
        this.thresholdMin = thresholdMin;
        this.thresholdMax = thresholdMax;
        this.status = SensorStatus.ACTIVE;
    }

    public static Sensor create(Facility facility, SensorType type, String name, String unit,
                                 Double thresholdMin, Double thresholdMax) {
        return new Sensor(facility, type, name, unit, thresholdMin, thresholdMax);
    }

    public void update(String name, String unit, Double thresholdMin, Double thresholdMax, SensorStatus status) {
        this.name = name;
        this.unit = unit;
        this.thresholdMin = thresholdMin;
        this.thresholdMax = thresholdMax;
        this.status = status;
    }

    public boolean isActive() {
        return status == SensorStatus.ACTIVE;
    }

    /** 임계치를 벗어났는지 판단한다. 어느 한쪽 기준만 있어도 동작한다. */
    public boolean breaches(double value) {
        if (thresholdMin != null && value < thresholdMin) {
            return true;
        }
        return thresholdMax != null && value > thresholdMax;
    }
}

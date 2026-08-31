package com.infrawatch.facility.domain;

import com.infrawatch.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "facility")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Facility extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private FacilityType type;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "description", length = 500)
    private String description;

    private Facility(String name, FacilityType type, String location, String description) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
    }

    public static Facility create(String name, FacilityType type, String location, String description) {
        return new Facility(name, type, location, description);
    }

    public void update(String name, FacilityType type, String location, String description) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.description = description;
    }
}

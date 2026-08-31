package com.infrawatch.facility.dto;

import com.infrawatch.facility.domain.Facility;

public record FacilityResponse(
        Long id,
        String name,
        String type,
        String location,
        String description,
        long sensorCount,
        long openAlertCount
) {

    public static FacilityResponse of(Facility facility, long sensorCount, long openAlertCount) {
        return new FacilityResponse(
                facility.getId(),
                facility.getName(),
                facility.getType().name(),
                facility.getLocation(),
                facility.getDescription(),
                sensorCount,
                openAlertCount);
    }
}

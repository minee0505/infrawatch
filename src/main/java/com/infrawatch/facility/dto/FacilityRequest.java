package com.infrawatch.facility.dto;

import com.infrawatch.facility.domain.FacilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FacilityRequest(
        @NotBlank @Size(max = 100) String name,
        @NotNull FacilityType type,
        @Size(max = 200) String location,
        @Size(max = 500) String description
) {
}

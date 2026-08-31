package com.infrawatch.member.dto;

import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull Boolean admin
) {
}

package com.infrawatch.audit.dto;

import com.infrawatch.audit.domain.AuditLog;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String actorName,
        String action,
        String resourceType,
        String resourceId,
        String detail,
        LocalDateTime createdAt
) {

    public static AuditLogResponse from(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getActor() != null ? log.getActor().getName() : "system",
                log.getAction(),
                log.getResourceType(),
                log.getResourceId(),
                log.getDetail(),
                log.getCreatedAt());
    }
}

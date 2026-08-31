package com.infrawatch.audit.repository;

import com.infrawatch.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}

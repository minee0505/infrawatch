package com.infrawatch.audit.service;

import com.infrawatch.audit.domain.AuditLog;
import com.infrawatch.audit.dto.AuditLogResponse;
import com.infrawatch.audit.repository.AuditLogRepository;
import com.infrawatch.member.domain.Member;
import com.infrawatch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final MemberRepository memberRepository;

    public List<AuditLogResponse> getLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream().map(AuditLogResponse::from).toList();
    }

    @Transactional
    public void record(Long actorId, String action, String resourceType, String resourceId, String detail) {
        Member actor = actorId != null ? memberRepository.findById(actorId).orElse(null) : null;
        auditLogRepository.save(AuditLog.of(actor, action, resourceType, resourceId, detail));
    }
}

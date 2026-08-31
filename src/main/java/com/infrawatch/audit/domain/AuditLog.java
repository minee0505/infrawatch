package com.infrawatch.audit.domain;

import com.infrawatch.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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

import java.time.LocalDateTime;

/** 누가 무엇을 했는지 남기는 불변 이력이다. 수정 시각이 필요 없어 BaseTimeEntity 를 쓰지 않는다. */
@Getter
@Entity
@Table(name = "audit_log")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private Member actor;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "resource_type", nullable = false, length = 30)
    private String resourceType;

    @Column(name = "resource_id", length = 50)
    private String resourceId;

    @Column(name = "detail", length = 500)
    private String detail;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private AuditLog(Member actor, String action, String resourceType, String resourceId, String detail) {
        this.actor = actor;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.detail = detail;
    }

    public static AuditLog of(Member actor, String action, String resourceType, String resourceId, String detail) {
        return new AuditLog(actor, action, resourceType, resourceId, detail);
    }
}

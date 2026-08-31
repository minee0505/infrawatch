package com.infrawatch.auth.security;

import com.infrawatch.member.domain.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * 인증된 사용자 정보. 컨트롤러와 서비스는 이 객체만 보고 "누가 요청했는지" 판단한다.
 */
@Getter
public class MemberPrincipal {

    private final Long memberId;
    private final Role role;

    public MemberPrincipal(Long memberId, Role role) {
        this.memberId = memberId;
        this.role = role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public boolean isAdmin() {
        return role == Role.ROLE_ADMIN;
    }
}

package com.infrawatch.member.domain;

/**
 * 스프링 시큐리티의 hasRole("ADMIN") 은 권한 문자열 "ROLE_ADMIN" 을 찾는다.
 * 그래서 enum 이름 자체에 ROLE_ 접두사를 붙여 두고 그대로 권한으로 사용한다.
 */
public enum Role {

    /** 총관리자. 시설물·센서·회원을 관리한다. */
    ROLE_ADMIN,

    /** 현장 관리자. 측정값과 알림을 조회하고 알림을 처리한다. */
    ROLE_FIELD_MANAGER
}

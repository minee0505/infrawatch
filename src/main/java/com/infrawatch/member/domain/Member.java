package com.infrawatch.member.domain;

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
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    /** BCrypt 로 해시된 값만 저장한다. */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    private Member(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public static Member create(String email, String encodedPassword, String name, Role role) {
        return new Member(email, encodedPassword, name, role);
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == Role.ROLE_ADMIN;
    }
}

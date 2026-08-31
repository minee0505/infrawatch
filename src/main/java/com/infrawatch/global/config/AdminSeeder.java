package com.infrawatch.global.config;

import com.infrawatch.member.domain.Member;
import com.infrawatch.member.domain.Role;
import com.infrawatch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 최초 기동 시 회원이 하나도 없으면 데모용 총관리자 계정을 만든다.
 * 비밀번호를 SQL 마이그레이션에 평문 또는 미리 계산한 해시로 넣지 않기 위해
 * 애플리케이션이 뜰 때 PasswordEncoder 로 직접 해시한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private static final String SEED_EMAIL = "admin@infrawatch.io";
    private static final String SEED_PASSWORD = "admin1234!";
    private static final String SEED_NAME = "총관리자";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (memberRepository.count() > 0) {
            return;
        }

        memberRepository.save(Member.create(
                SEED_EMAIL, passwordEncoder.encode(SEED_PASSWORD), SEED_NAME, Role.ROLE_ADMIN));
        log.info("데모 총관리자 계정을 생성했습니다: {} / {}", SEED_EMAIL, SEED_PASSWORD);
    }
}

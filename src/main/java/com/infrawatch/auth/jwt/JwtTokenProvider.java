package com.infrawatch.auth.jwt;

import com.infrawatch.member.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

/**
 * JWT 발급과 검증을 담당한다.
 * 서버가 로그인 상태를 들고 있지 않으므로 서버를 여러 대로 늘려도 세션 공유가 필요 없다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String CLAIM_ROLE = "role";

    private final SecretKey key;
    private final Duration validity;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.validity-seconds}") long validitySeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validity = Duration.ofSeconds(validitySeconds);
    }

    public String createToken(Long memberId, Role role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(CLAIM_ROLE, role.name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰이 유효하면 담긴 정보를 반환하고, 유효하지 않으면 null 을 반환한다.
     */
    public JwtPayload parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new JwtPayload(
                    Long.valueOf(claims.getSubject()),
                    Role.valueOf(claims.get(CLAIM_ROLE, String.class)));

        } catch (JwtException | IllegalArgumentException e) {
            log.debug("유효하지 않은 토큰: {}", e.getMessage());
            return null;
        }
    }

    public record JwtPayload(Long memberId, Role role) {
    }
}

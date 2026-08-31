package com.infrawatch.auth.service;

import com.infrawatch.auth.dto.LoginRequest;
import com.infrawatch.auth.dto.RegisterRequest;
import com.infrawatch.auth.dto.TokenResponse;
import com.infrawatch.auth.jwt.JwtTokenProvider;
import com.infrawatch.global.error.BusinessException;
import com.infrawatch.global.error.ErrorCode;
import com.infrawatch.member.domain.Member;
import com.infrawatch.member.domain.Role;
import com.infrawatch.member.dto.MemberResponse;
import com.infrawatch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /** 자가 가입은 현장 관리자 권한으로만 열려 있다. 총관리자는 시딩되거나 기존 관리자가 승격한다. */
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member member = Member.create(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name(),
                Role.ROLE_FIELD_MANAGER);
        memberRepository.save(member);

        return issueToken(member);
    }

    public TokenResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return issueToken(member);
    }

    public MemberResponse me(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberResponse.from(member);
    }

    private TokenResponse issueToken(Member member) {
        String token = jwtTokenProvider.createToken(member.getId(), member.getRole());
        return new TokenResponse(token, MemberResponse.from(member));
    }
}

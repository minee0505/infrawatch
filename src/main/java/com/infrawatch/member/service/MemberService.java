package com.infrawatch.member.service;

import com.infrawatch.global.error.BusinessException;
import com.infrawatch.global.error.ErrorCode;
import com.infrawatch.member.domain.Member;
import com.infrawatch.member.domain.Role;
import com.infrawatch.member.dto.MemberResponse;
import com.infrawatch.member.dto.RoleUpdateRequest;
import com.infrawatch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> getMembers() {
        return memberRepository.findAll().stream().map(MemberResponse::from).toList();
    }

    @Transactional
    public MemberResponse updateRole(Long memberId, RoleUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.changeRole(request.admin() ? Role.ROLE_ADMIN : Role.ROLE_FIELD_MANAGER);
        return MemberResponse.from(member);
    }
}

package com.infrawatch.member.controller;

import com.infrawatch.member.dto.MemberResponse;
import com.infrawatch.member.dto.RoleUpdateRequest;
import com.infrawatch.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberResponse> getMembers() {
        return memberService.getMembers();
    }

    @PatchMapping("/{memberId}/role")
    public MemberResponse updateRole(@PathVariable Long memberId, @RequestBody RoleUpdateRequest request) {
        return memberService.updateRole(memberId, request);
    }
}

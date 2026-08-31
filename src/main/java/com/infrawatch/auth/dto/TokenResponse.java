package com.infrawatch.auth.dto;

import com.infrawatch.member.dto.MemberResponse;

public record TokenResponse(
        String accessToken,
        MemberResponse member
) {
}

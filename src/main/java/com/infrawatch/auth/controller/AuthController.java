package com.infrawatch.auth.controller;

import com.infrawatch.auth.dto.LoginRequest;
import com.infrawatch.auth.dto.RegisterRequest;
import com.infrawatch.auth.dto.TokenResponse;
import com.infrawatch.auth.security.MemberPrincipal;
import com.infrawatch.auth.service.AuthService;
import com.infrawatch.member.dto.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public MemberResponse me(@AuthenticationPrincipal MemberPrincipal principal) {
        return authService.me(principal.getMemberId());
    }
}

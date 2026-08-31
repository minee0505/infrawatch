package com.infrawatch.global.config;

import com.infrawatch.auth.handler.RestAuthenticationEntryPoint;
import com.infrawatch.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 토큰 기반 인증이라 쿠키를 자동으로 실어 보내지 않는다. CSRF 공격 조건이 성립하지 않으므로 끈다.
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증 결과를 세션에 남기지 않는다. 요청마다 토큰만 보고 판단한다.
                .securityContext(context -> context
                        .securityContextRepository(new RequestAttributeSecurityContextRepository()))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 프론트엔드 화면과 정적 파일. 화면 자체는 누구나 받을 수 있고,
                        // 그 안에서 부르는 API 가 인증을 요구한다.
                        .requestMatchers(HttpMethod.GET, "/index.html", "/assets/**", "/favicon.svg").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login", "/dashboard", "/facilities", "/facilities/*",
                                "/alerts", "/members", "/audit-logs").permitAll()

                        .requestMatchers("/api/v1/members/**", "/api/v1/audit-logs/**").hasRole("ADMIN")

                        .anyRequest().authenticated())

                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAuthenticationEntryPoint))

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

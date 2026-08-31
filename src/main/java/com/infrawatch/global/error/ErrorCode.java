package com.infrawatch.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "F001", "시설물을 찾을 수 없습니다."),

    SENSOR_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "센서를 찾을 수 없습니다."),
    SENSOR_INACTIVE(HttpStatus.BAD_REQUEST, "S002", "비활성 상태인 센서입니다."),

    ALERT_NOT_FOUND(HttpStatus.NOT_FOUND, "AL001", "알림을 찾을 수 없습니다."),
    ALERT_ALREADY_RESOLVED(HttpStatus.CONFLICT, "AL002", "이미 종료된 알림입니다."),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "M001", "이미 사용 중인 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "M002", "이메일 또는 비밀번호가 올바르지 않습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M003", "회원을 찾을 수 없습니다."),

    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "C001", "로그인이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C002", "이 작업을 수행할 권한이 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C003", "입력값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

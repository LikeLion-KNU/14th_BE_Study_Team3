package com.example.study.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "INVALID_INPUT", "요청 값이 올바르지 않습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "일시적인 오류가 발생했습니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "JSON_PARSE_ERROR", "요청 본문을 해석할 수 없습니다."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", "이미 존재하는 데이터입니다."),

    // User Domain
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 사용자입니다."),

    // Course Domain
    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND", "존재하지 않는 강의입니다."),

    // Enrollment Domain
    ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ENROLLMENT_NOT_FOUND", "존재하지 않는 수강신청 내역입니다."),
    ALREADY_ENROLLED(HttpStatus.CONFLICT, "ALREADY_ENROLLED", "이미 수강신청이 완료된 강의입니다.");

    public final HttpStatus status;
    public final String code;
    public final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

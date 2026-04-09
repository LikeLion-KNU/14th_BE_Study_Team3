package com.example.study.domain.user.exception;

import org.springframework.http.HttpStatus;

import com.example.study.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserDomainErrorCode implements ErrorCode {
    
    // user domain error code
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "NOT_FOUND_USER", "해당하는 아이디의 유저가 존재하지 않습니다."),
    NOT_VALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "NOT_VALID_REQUEST_BODY", "유효하지 않은 요청 바디입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

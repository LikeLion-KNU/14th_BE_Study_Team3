package com.example.study.domain.course.exception;

import com.example.study.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements ErrorCode {

    COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_NOT_FOUND", "해당 강의를 찾을 수 없습니다."),
    COURSE_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "COURSE_CAPACITY_EXCEEDED", "수강 인원이 정원을 초과할 수 없습니다."),
    COURSE_ENROLLMENT_NEGATIVE(HttpStatus.BAD_REQUEST, "COURSE_ENROLLMENT_NEGATIVE", "수강 인원은 0보다 작을 수 없습니다."),
    COURSE_INVALID_CAPACITY(HttpStatus.BAD_REQUEST, "COURSE_INVALID_CAPACITY", "정원은 1 이상 이어야 합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

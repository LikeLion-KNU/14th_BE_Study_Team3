package com.example.study.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 명시적 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, WebRequest req) {
        ErrorCode code = ex.getErrorCode();
        ErrorResponse body = ErrorResponse.of(code, ex.getMessage(), Collections.emptyList(),
                path(req));
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // DTO 검증 실패 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest req) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::formatFieldError)
                .collect(Collectors.toList());

        ErrorCode code = CommonErrorCode.INVALID_INPUT;

        ErrorResponse body = ErrorResponse.of(code, code.getMessage(), errors, path(req));
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // JSON 파싱 에러
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                              WebRequest req) {
        ErrorCode code = CommonErrorCode.JSON_PARSE_ERROR;
        ErrorResponse body = ErrorResponse.of(code, code.getMessage(), Collections.emptyList(),
                path(req));
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // DB 무결성 위반
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                WebRequest req) {
        ErrorCode code = CommonErrorCode.DUPLICATE_RESOURCE;
        ErrorResponse body = ErrorResponse.of(code, code.getMessage(), Collections.emptyList(),
                path(req));
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    // 마지막 안전망
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, WebRequest req) {
        // 로그
        String description = req.getDescription(false);
        String path = description.replace("uri=", "");
        log.error("Internal error at {}: {}", path, ex.getMessage(), ex);

        ErrorCode code = CommonErrorCode.INTERNAL_ERROR;
        ErrorResponse body = ErrorResponse.of(code, code.getMessage(), Collections.emptyList(),
                path(req));
        return ResponseEntity.status(code.getStatus()).body(body);
    }

    private static String formatFieldError(FieldError fe) {
        return fe.getField() + ": " + fe.getDefaultMessage();
    }

    private static String path(WebRequest req) {
        String desc = req.getDescription(false);
        if (desc != null && desc.startsWith("uri=")) {
            return desc.substring(4);
        }
        return desc;
    }
}
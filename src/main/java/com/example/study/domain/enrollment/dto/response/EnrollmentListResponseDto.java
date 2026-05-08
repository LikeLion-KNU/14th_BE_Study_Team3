package com.example.study.domain.enrollment.dto.response;

import java.util.List;

public record EnrollmentListResponseDto(
        List<EnrollmentResponseDto> enrollments,
        Integer totalCount
) {
    public static EnrollmentListResponseDto of(List<EnrollmentResponseDto> enrollments) {
        return new EnrollmentListResponseDto(enrollments, enrollments.size());
    }
}

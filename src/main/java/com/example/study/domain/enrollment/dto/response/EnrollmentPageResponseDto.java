package com.example.study.domain.enrollment.dto.response;

import java.util.List;

public record EnrollmentPageResponseDto(
        List<EnrollmentResponseDto> enrollments,
        int page,
        int size,
        boolean hasNext
) {
}

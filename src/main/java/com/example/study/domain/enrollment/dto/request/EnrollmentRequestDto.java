package com.example.study.domain.enrollment.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequestDto(
        @NotNull(message = "유저 ID는 필수입니다.")
        Long userId,

        @NotNull(message = "강의 ID는 필수입니다.")
        Long courseId
) {
}

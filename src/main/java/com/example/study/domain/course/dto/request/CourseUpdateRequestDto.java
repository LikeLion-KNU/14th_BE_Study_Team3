package com.example.study.domain.course.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseUpdateRequestDto(
        @Size(max = 64, message = "강의 이름은 64자 이하입니다.")
        String name,

        @Min(value = 1, message = "수강 정원은 최소 1 이상이어야 합니다.")
        Integer capacity
) {
}

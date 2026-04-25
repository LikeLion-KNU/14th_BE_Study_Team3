package com.example.study.domain.course.dto.response;

import java.util.List;

public record CoursePageResponseDto(
        List<CourseResponseDto> courses,
        int page,
        int size,
        boolean hasNext
) {
}

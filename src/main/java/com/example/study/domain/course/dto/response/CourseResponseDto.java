package com.example.study.domain.course.dto.response;

import com.example.study.domain.course.entity.Course;

public record CourseResponseDto(
        Long id,
        String name,
        Integer enrolledCount,
        Integer capacity
) {
    public static CourseResponseDto from(Course course) {
        return new CourseResponseDto(
                course.getId(),
                course.getName(),
                course.getEnrolledCount(),
                course.getCapacity()
        );
    }
}

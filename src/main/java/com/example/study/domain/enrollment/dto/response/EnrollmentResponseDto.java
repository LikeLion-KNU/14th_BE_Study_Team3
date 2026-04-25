package com.example.study.domain.enrollment.dto.response;

import com.example.study.domain.enrollment.entity.Enrollment;

public record EnrollmentResponseDto(
        long id,
        String userName,
        String courseName,
        int enrolledCount,
        int capacity
) {
    public static EnrollmentResponseDto from(Enrollment enrollment) {
        return new EnrollmentResponseDto(
                enrollment.getId(),
                enrollment.getUser().getName(),
                enrollment.getCourse().getName(),
                enrollment.getCourse().getEnrolledCount(),
                enrollment.getCourse().getCapacity()
        );
    }
}

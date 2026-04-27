package com.example.study.domain.enrollment.service;

import com.example.study.domain.enrollment.dto.request.EnrollmentRequestDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentListResponseDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentPageResponseDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentResponseDto;
import org.springframework.data.domain.Pageable;


public interface EnrollmentService {
    EnrollmentResponseDto createEnrollment(EnrollmentRequestDto request);

    EnrollmentPageResponseDto findAllEnrollments(Pageable pageable);

    EnrollmentListResponseDto findEnrollmentsByUserId(Long userId);

    EnrollmentListResponseDto findEnrollmentsByCourseId(Long courseId);

    void deleteEnrollment(Long enrollmentId);
}

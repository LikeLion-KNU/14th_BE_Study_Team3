package com.example.study.domain.enrollment.service.facade;

import com.example.study.domain.enrollment.dto.request.EnrollmentRequestDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentResponseDto;
import com.example.study.domain.enrollment.service.EnrollmentService;
import com.example.study.global.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedLockFacade {

    private final EnrollmentService enrollmentService;

    @DistributedLock(key = "#requestDto.courseId")
    public EnrollmentResponseDto createEnrollment(EnrollmentRequestDto requestDto) {
        return enrollmentService.createEnrollment(requestDto);
    }
}

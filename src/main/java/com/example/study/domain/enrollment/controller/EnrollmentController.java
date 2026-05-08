package com.example.study.domain.enrollment.controller;

import com.example.study.domain.enrollment.dto.request.EnrollmentRequestDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentListResponseDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentPageResponseDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentResponseDto;
import com.example.study.domain.enrollment.service.EnrollmentService;
import com.example.study.domain.enrollment.service.facade.DistributedLockFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // redis 분산 락 적용
    private final DistributedLockFacade distributedLockFacade;

    @PostMapping("/enrollments")
    public ResponseEntity<EnrollmentResponseDto> createEnrollment(
            @RequestBody @Valid EnrollmentRequestDto request) {

        EnrollmentResponseDto response = enrollmentService.createEnrollment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/enrollments/distributed")
    public ResponseEntity<EnrollmentResponseDto> enrollWithDistributedLock(
            @RequestBody EnrollmentRequestDto requestDto) {

        return ResponseEntity.ok(distributedLockFacade.createEnrollment(requestDto));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<EnrollmentPageResponseDto> getAllEnrollments(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok(enrollmentService.findAllEnrollments(pageable));
    }

    @GetMapping("/users/{id}/enrollments")
    public ResponseEntity<EnrollmentListResponseDto> getUserEnrollments(@PathVariable("id") Long userId) {

        return ResponseEntity.ok(enrollmentService.findEnrollmentsByUserId(userId));
    }

    @GetMapping("/courses/{id}/enrollments")
    public ResponseEntity<EnrollmentListResponseDto> getCourseEnrollments(@PathVariable("id") Long courseId) {
        return ResponseEntity.ok(enrollmentService.findEnrollmentsByCourseId(courseId));
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable("id") Long enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }
}
package com.example.study.domain.enrollment.service;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.exception.CourseErrorCode;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.domain.enrollment.dto.request.EnrollmentRequestDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentListResponseDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentPageResponseDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentResponseDto;
import com.example.study.domain.enrollment.entity.Enrollment;
import com.example.study.domain.enrollment.exception.EnrollmentErrorCode;
import com.example.study.domain.enrollment.repository.EnrollmentRepository;
import com.example.study.domain.user.User;
import com.example.study.domain.user.repository.UserRepository;
import com.example.study.domain.user.exception.UserDomainErrorCode;
import com.example.study.global.exception.BusinessException;
import com.example.study.global.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentServiceImpl implements EnrollmentService{

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    @Override
    public EnrollmentResponseDto createEnrollment(EnrollmentRequestDto request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BusinessException(UserDomainErrorCode.NOT_FOUND_USER));

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new BusinessException(EnrollmentErrorCode.ALREADY_ENROLLED);
        }

        if (course.getEnrolledCount() >= course.getCapacity()) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT, "수강 인원이 마감되었습니다.");
        }

        course.increaseEnrolledCount();
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return EnrollmentResponseDto.from(savedEnrollment);
    }

    @Override
    public EnrollmentPageResponseDto findAllEnrollments(Pageable pageable) {
        Slice<Enrollment> enrollmentPage = enrollmentRepository.findAllBy(pageable);

        List<EnrollmentResponseDto> enrollments = enrollmentPage
                .map(EnrollmentResponseDto::from)
                .toList();

        return new EnrollmentPageResponseDto(
                enrollments,
                enrollmentPage.getNumber(),
                enrollmentPage.getSize(),
                enrollmentPage.hasNext()
        );
    }

    @Override
    public EnrollmentListResponseDto findEnrollmentsByUserId(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByUserId(userId);

        return convertToListResponseDto(enrollments);
    }

    @Override
    public EnrollmentListResponseDto findEnrollmentsByCourseId(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);

        return convertToListResponseDto(enrollments);
    }

    @Transactional
    @Override
    public void deleteEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(EnrollmentErrorCode.ENROLLMENT_NOT_FOUND));

        enrollment.getCourse().decreaseEnrolledCount();
        enrollmentRepository.delete(enrollment);
    }

    private EnrollmentListResponseDto convertToListResponseDto(List<Enrollment> enrollments) {
        List<EnrollmentResponseDto> dtoList = enrollments.stream()
                .map(EnrollmentResponseDto::from)
                .collect(Collectors.toList());

        return EnrollmentListResponseDto.of(dtoList);
    }
}

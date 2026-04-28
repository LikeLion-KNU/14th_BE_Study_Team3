package com.example.study.domain.enrollment;


import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.domain.enrollment.dto.request.EnrollmentRequestDto;
import com.example.study.domain.enrollment.dto.response.EnrollmentResponseDto;
import com.example.study.domain.enrollment.entity.Enrollment;
import com.example.study.domain.enrollment.repository.EnrollmentRepository;
import com.example.study.domain.enrollment.service.EnrollmentServiceImpl;
import com.example.study.domain.user.User;
import com.example.study.domain.user.UserRepository;
import com.example.study.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceImplTest {

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private CourseRepository courseRepository;

    @Test
    @DisplayName("수강 신청 성공")
    void createEnrollment_success() {
        // given
        Long userId = 1L;
        Long courseId = 2L;
        EnrollmentRequestDto request = new EnrollmentRequestDto(userId, courseId);

        User user = User.builder().name("테스트유저").build();
        ReflectionTestUtils.setField(user, "id", userId);

        Course course = Course.builder().name("스프링 강의").capacity(10).enrolledCount(0).build();
        ReflectionTestUtils.setField(course, "id", courseId);

        Enrollment enrollment = Enrollment.builder().user(user).course(course).build();
        ReflectionTestUtils.setField(enrollment, "id", 100L);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(courseRepository.findById(courseId)).willReturn(Optional.of(course));
        given(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).willReturn(false);
        given(enrollmentRepository.save(any(Enrollment.class))).willReturn(enrollment);

        // when
        EnrollmentResponseDto response = enrollmentService.createEnrollment(request);

        // then
        assertThat(response.id()).isEqualTo(100L);
        assertThat(course.getEnrolledCount()).isEqualTo(1);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("수강 신청 실패 - 이미 수강 중인 강의")
    void createEnrollment_fail_already_enrolled() {
        // given
        EnrollmentRequestDto request = new EnrollmentRequestDto(1L, 2L);
        User user = User.builder().build();
        ReflectionTestUtils.setField(user, "id", 1L);
        Course course = Course.builder().capacity(10).enrolledCount(0).build();
        ReflectionTestUtils.setField(course, "id", 2L);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(courseRepository.findById(2L)).willReturn(Optional.of(course));
        given(enrollmentRepository.existsByUserIdAndCourseId(1L, 2L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> enrollmentService.createEnrollment(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("수강 취소 성공")
    void deleteEnrollment_success() {
        // given
        Long enrollmentId = 1L;
        User user = User.builder().build();
        Course course = Course.builder().capacity(10).enrolledCount(5).build();
        Enrollment enrollment = Enrollment.builder().user(user).course(course).build();

        given(enrollmentRepository.findById(enrollmentId)).willReturn(Optional.of(enrollment));

        // when
        enrollmentService.deleteEnrollment(enrollmentId);

        // then
        assertThat(course.getEnrolledCount()).isEqualTo(4);
        verify(enrollmentRepository).delete(enrollment);
    }
}

package com.example.study.domain.course;

import com.example.study.domain.course.dto.request.CourseCreateRequestDto;
import com.example.study.domain.course.dto.request.CourseUpdateRequestDto;
import com.example.study.domain.course.dto.response.CourseResponseDto;
import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.domain.course.service.CourseServiceImpl;
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
public class CourseServiceImplTest {

    @InjectMocks
    private CourseServiceImpl courseService;

    @Mock
    private CourseRepository courseRepository;

    @Test
    @DisplayName("강의 생성 성공")
    void createCourse_success() {
        // given
        CourseCreateRequestDto request = new CourseCreateRequestDto("스프링 부트 강의", 30);
        Course course = Course.builder().name(request.name()).capacity(request.capacity()).enrolledCount(0).build();
        ReflectionTestUtils.setField(course, "id", 1L);

        given(courseRepository.save(any(Course.class))).willReturn(course);

        // when
        CourseResponseDto response = courseService.createCourse(request);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("스프링 부트 강의");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("강의 수정 성공")
    void updateCourse_success() {
        // given
        Long courseId = 1L;
        CourseUpdateRequestDto request = new CourseUpdateRequestDto("수정된 강의명", 50);
        Course course = Course.builder().name("기존 강의명").capacity(30).enrolledCount(0).build();

        given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

        // when
        CourseResponseDto response = courseService.updateCourse(courseId, request);

        // then
        assertThat(response.name()).isEqualTo("수정된 강의명");
        assertThat(response.capacity()).isEqualTo(50);
    }

    @Test
    @DisplayName("강의 조회 실패 - 존재하지 않는 강의")
    void findCourseById_fail_not_found() {
        // given
        Long invalidId = 999L;
        given(courseRepository.findById(invalidId)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> courseService.findCourseById(invalidId))
                .isInstanceOf(BusinessException.class);
    }
}

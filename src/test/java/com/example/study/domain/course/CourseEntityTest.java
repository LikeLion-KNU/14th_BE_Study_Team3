package com.example.study.domain.course;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.exception.CourseErrorCode;
import com.example.study.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CourseEntityTest {

    @Test
    @DisplayName("강의 정보 수정 성공 - 이름과 정원 모두 변경")
    void updateCourse_success() {
        // given
        Course course = Course.builder()
                .name("기존 강의")
                .capacity(10)
                .enrolledCount(5)
                .build();

        // when
        course.updateCourse("수정된 강의", 20);

        // then
        assertThat(course.getName()).isEqualTo("수정된 강의");
        assertThat(course.getCapacity()).isEqualTo(20);
    }

    @Test
    @DisplayName("강의 정보 수정 실패 - 정원을 현재 수강 인원보다 적게 변경 시도")
    void updateCourse_fail_capacity_exceeded() {
        // given
        Course course = Course.builder()
                .name("강의")
                .capacity(10)
                .enrolledCount(8)
                .build();

        // when, then
        assertThatThrownBy(() -> course.updateCourse(null, 5))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CourseErrorCode.COURSE_CAPACITY_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("수강 인원 증가 성공")
    void increaseEnrolledCount_success() {
        // given
        Course course = Course.builder().name("강의").capacity(2).enrolledCount(1).build();

        // when
        course.increaseEnrolledCount();

        // then
        assertThat(course.getEnrolledCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("수강 인원 증가 실패 - 정원 초과")
    void increaseEnrolledCount_fail_capacity_exceeded() {
        // given
        Course course = Course.builder().name("강의").capacity(2).enrolledCount(2).build();

        // when, then
        assertThatThrownBy(course::increaseEnrolledCount)
                .isInstanceOf(BusinessException.class);
    }
}

package com.example.study.domain.course.service;

import com.example.study.domain.course.dto.request.CourseCreateRequestDto;
import com.example.study.domain.course.dto.request.CourseUpdateRequestDto;
import com.example.study.domain.course.dto.response.CoursePageResponseDto;
import com.example.study.domain.course.dto.response.CourseResponseDto;
import org.springframework.data.domain.Pageable;

public interface CourseService {
    CourseResponseDto createCourse(CourseCreateRequestDto request);
    CoursePageResponseDto findAllCourses(Pageable pageable);
    CourseResponseDto findCourseById(Long id);
    CourseResponseDto updateCourse(Long id, CourseUpdateRequestDto request);
    void deleteCourse(Long id);
}
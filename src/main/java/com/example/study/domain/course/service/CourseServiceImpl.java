package com.example.study.domain.course.service;

import com.example.study.domain.course.dto.request.CourseCreateRequestDto;
import com.example.study.domain.course.dto.request.CourseUpdateRequestDto;
import com.example.study.domain.course.dto.response.CoursePageResponseDto;
import com.example.study.domain.course.dto.response.CourseResponseDto;
import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.exception.CourseErrorCode;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Transactional
    @Override
    public CourseResponseDto createCourse(CourseCreateRequestDto request) {
        Course course = Course.builder()
                .name(request.name())
                .capacity(request.capacity())
                .enrolledCount(0)
                .build();
        
        Course savedCourse = courseRepository.save(course);
        return CourseResponseDto.from(savedCourse);
    }

    @Override
    public CoursePageResponseDto findAllCourses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findAll(pageable);
        
        List<CourseResponseDto> courses = coursePage.stream()
                .map(CourseResponseDto::from)
                .collect(Collectors.toList());
                
        return new CoursePageResponseDto(
                courses,
                coursePage.getNumber(),
                coursePage.getSize(),
                coursePage.hasNext()
        );
    }

    @Override
    public CourseResponseDto findCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));

        return CourseResponseDto.from(course);
    }

    @Transactional
    @Override
    public CourseResponseDto updateCourse(Long id, CourseUpdateRequestDto request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));
                
        course.updateCourse(request.name(), request.capacity());

        return CourseResponseDto.from(course);
    }

    @Transactional
    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CourseErrorCode.COURSE_NOT_FOUND));
                
        courseRepository.delete(course);
    }
}
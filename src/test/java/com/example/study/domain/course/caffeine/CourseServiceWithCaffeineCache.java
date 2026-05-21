package com.example.study.domain.course.caffeine;

import com.example.study.domain.course.dto.response.CoursePageResponseDto;
import com.example.study.domain.course.dto.response.CourseResponseDto;
import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceWithCaffeineCache {

    private final CourseRepository courseRepository;

    public CourseServiceWithCaffeineCache(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Cacheable(value = "courses", key = "#pageable.pageNumber")
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
}

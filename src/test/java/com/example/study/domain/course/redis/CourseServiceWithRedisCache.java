package com.example.study.domain.course.redis;

import com.example.study.domain.course.dto.response.CoursePageResponseDto;
import com.example.study.domain.course.dto.response.CourseResponseDto;
import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceWithRedisCache {

    private final CourseRepository courseRepository;

    /**
     * 인기 과목 목록 조회 (Redis 캐시 적용)
     * value: Redis에 저장될 캐시 이름 ("courses")
     * key: 페이지네이션 처리를 위해 페이지 번호를 키로 활용
     */
    @Cacheable(value = "courses", key = "#pageable.pageNumber", cacheManager = "cacheManager")
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
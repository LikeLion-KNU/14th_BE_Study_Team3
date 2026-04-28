package com.example.study.domain.course.controller;

import com.example.study.domain.course.dto.request.CourseCreateRequestDto;
import com.example.study.domain.course.dto.request.CourseUpdateRequestDto;
import com.example.study.domain.course.dto.response.CoursePageResponseDto;
import com.example.study.domain.course.dto.response.CourseResponseDto;
import com.example.study.domain.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponseDto> createCourse(
            @RequestBody @Valid CourseCreateRequestDto request) {

        CourseResponseDto response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CoursePageResponseDto> getAllCourses(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        CoursePageResponseDto response = courseService.findAllCourses(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDto> getCourseById(@PathVariable("id") Long id) {

        CourseResponseDto response = courseService.findCourseById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable("id") Long id,
            @RequestBody CourseUpdateRequestDto request) {

        CourseResponseDto response = courseService.updateCourse(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("id") Long id) {

        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}

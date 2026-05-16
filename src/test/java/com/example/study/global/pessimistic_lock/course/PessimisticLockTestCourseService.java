package com.example.study.global.pessimistic_lock.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.study.domain.course.entity.Course;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessimisticLockTestCourseService {
    final private PessimisticLockTestCourseRepository courseRepository;

    @Transactional(readOnly = true)
    public Course findById(long id) {
        return courseRepository.findById(id).orElseThrow();
    }
        
    @Transactional
    public void create(Course course) {
        courseRepository.save(course);
        return;
    }

    @Transactional
    public void increaseEnrolledCount(long id) {
        Course course = courseRepository.findById(id).orElseThrow();
        course.increaseEnrolledCount();
        return;
    }

    @Transactional
    public void increaseEnrolledCountWithLock(long id) {
        Course course = courseRepository.findByIdWithPessimisticWrite(id).orElseThrow();
        course.increaseEnrolledCount();
        return;
    }

    @Transactional
    public void deleteAll() {
        courseRepository.deleteAll();
        return;
    }
}

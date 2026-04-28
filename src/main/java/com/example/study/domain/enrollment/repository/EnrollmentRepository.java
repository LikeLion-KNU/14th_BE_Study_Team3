package com.example.study.domain.enrollment.repository;

import com.example.study.domain.enrollment.entity.Enrollment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Slice<Enrollment> findAllBy(Pageable pageable);

    List<Enrollment> findAllByUserId(Long userId);

    List<Enrollment> findAllByCourseId(Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}

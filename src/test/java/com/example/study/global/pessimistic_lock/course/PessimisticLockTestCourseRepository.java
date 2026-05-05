package com.example.study.global.pessimistic_lock.course;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.study.domain.course.entity.Course;

import jakarta.persistence.LockModeType;

interface PessimisticLockTestCourseRepository extends JpaRepository<Course, Long> {
    // course를 가져올 때 그 행의 배타적 잠금을 획득
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Course As c where c.id = :id")
    Optional<Course> findByIdWithPessimisticWrite(@Param("id") long id);
}

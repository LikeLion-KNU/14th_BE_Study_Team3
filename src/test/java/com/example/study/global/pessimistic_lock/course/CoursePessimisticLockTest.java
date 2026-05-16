package com.example.study.global.pessimistic_lock.course;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.study.domain.course.entity.Course;

@SpringBootTest
public class CoursePessimisticLockTest {
    final int userCount = 100; // 100명의 사용자가 수강 신청을 동시에 한다고 가정

    @Autowired
    PessimisticLockTestCourseService courseService;

    @Test
    @DisplayName("멀티스레드 환경에서 강의 수강 시 동시성 문제 발생")
    void happenConcurrencyIssueWhenIncreaseEnrollmentCount() throws InterruptedException {
        // Given
        final ExecutorService executor =  Executors.newFixedThreadPool(userCount);
        final CountDownLatch countDownLatch = new CountDownLatch(userCount);
        Course course = Course
            .builder()
            .name("testCourse")
            .enrolledCount(0)
            .capacity(userCount)
            .build();
        courseService.create(course);

        // When
        for (int i = 0; i < userCount; i++) {
            // 각 스레드에서 수강 신청 로직을 실행 -> 100명의 사용자가 동시에 수강 신청
            executor.submit(() -> {
                try {
                    courseService.increaseEnrolledCount(course.getId());
                } finally {
                    countDownLatch.countDown();   
                }
            });
        }
        countDownLatch.await();
        final Course updatedCourse = courseService.findById(course.getId());

        // Then
        assertThat(updatedCourse.getEnrolledCount()).isEqualTo(userCount);   
    }

    @Test
    @DisplayName("비관적 락을 사용해 멀티스레드 환경에서 강의 수강 시 발생하는 동시성 문제를 해결")
    void solveConcurrencyIssueWhenIncreaseEnrollmentCountUsingPessimisticLock() throws InterruptedException{
          // Given
        final ExecutorService executor =  Executors.newFixedThreadPool(userCount);
        final CountDownLatch countDownLatch = new CountDownLatch(userCount);
        Course course = Course
            .builder()
            .name("testCourse")
            .enrolledCount(0)
            .capacity(userCount)
            .build();
        courseService.create(course);

        // When
        for (int i = 0; i < userCount; i++) {
            // 각 스레드에서 수강 신청 로직을 실행 -> 100명의 사용자가 동시에 수강 신청
            executor.submit(() -> {
                try {
                    // 비관적 락을 사용해 수강 인원을 증가
                    courseService.increaseEnrolledCountWithLock(course.getId());
                } finally {
                    countDownLatch.countDown();   
                }
            });
        }
        countDownLatch.await();
        final Course updatedCourse = courseService.findById(course.getId());

        // Then
        assertThat(updatedCourse.getEnrolledCount()).isEqualTo(userCount);
    }

    @AfterEach
    void cleanDB() {
        courseService.deleteAll();
    }
}



package com.example.study.domain.enrollment.lock;


import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.domain.enrollment.dto.request.EnrollmentRequestDto;
import com.example.study.domain.enrollment.repository.EnrollmentRepository;
import com.example.study.domain.enrollment.service.facade.DistributedLockFacade;
import com.example.study.domain.user.User;
import com.example.study.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DistributedLockFacadeTest {

    @Autowired
    private DistributedLockFacade distributedLockFacade;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Course savedCourse;
    private List<User> savedUsers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 1. 수용 인원이 30명인 강의 생성
        Course course = Course.builder()
                .name("스프링부트 동시성 정복하기")
                .capacity(30)
                .enrolledCount(0)
                .build();
        savedCourse = courseRepository.save(course);

        // 2. 100명의 테스트 유저 생성
        for (int i = 0; i < 100; i++) {
            User user = User.builder()
                    .name("User" + i)
                    .build();
            savedUsers.add(userRepository.save(user));
        }
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 데이터 정리
        enrollmentRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        courseRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("100명의 유저가 동시에 수강신청을 요청하면 30명만 성공해야 한다.")
    void enrollConcurrencyTest() throws InterruptedException {
        // given
        int threadCount = 100;
        // 32개의 스레드를 가진 풀 생성 (동시 요청 시뮬레이션)
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 모든 스레드의 작업이 끝날 때까지 대기하기 위한 Latch
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() -> {
                try {
                    // 각기 다른 유저가 같은 강의에 수강신청 요청
                    EnrollmentRequestDto request = new EnrollmentRequestDto(
                            savedUsers.get(finalI).getId(),
                            savedCourse.getId()
                    );
                    distributedLockFacade.createEnrollment(request);
                } catch (Exception e) {
                    // 수용 인원 초과(INVALID_INPUT) 또는 락 획득 실패 시 예외가 발생하므로 무시
                    System.out.println("수강신청 실패: " + e.getMessage());
                } finally {
                    // 작업이 완료되면 Latch 감소 (예외가 발생하든 안 하든 무조건 실행되어야 함)
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 종료될 때까지 메인 스레드 대기
        latch.await();

        // then
        // 1. 강의의 현재 수강 인원이 정확히 30명이어야 함
        Course updatedCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        assertThat(updatedCourse.getEnrolledCount()).isEqualTo(30L);

        // 2. 실제 DB에 저장된 수강 내역의 개수도 정확히 30개여야 함
        long enrollmentCount = enrollmentRepository.count();
        assertThat(enrollmentCount).isEqualTo(30L);
    }
}

package com.example.study.domain.enrollment;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.domain.enrollment.entity.Enrollment;
import com.example.study.domain.enrollment.repository.EnrollmentRepository;
import com.example.study.domain.user.User;
import com.example.study.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class EnrollmentRaceConditionTest {

    @Autowired private CourseRepository courseRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PlatformTransactionManager txManager;


    @PersistenceContext
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("[레이스 컨디션] TX1과 TX2가 동시에 같은 데이터를 읽으면 정원이 초과된다")
    void RaceConditionTest() throws InterruptedException {

        Course course = courseRepository.save(
                Course.builder().name("강의").capacity(1).enrolledCount(0).build()
        );
        User user1 = userRepository.save(User.builder().name("user1").build());
        User user2 = userRepository.save(User.builder().name("user2").build());

        Long courseId = course.getId();
        Long user1Id  = user1.getId();
        Long user2Id  = user2.getId();

        //Tx1 읽기 카운트 다움
        CountDownLatch tx1ReadDone = new CountDownLatch(1);
        //Tx2 읽기 카운트 다운
        CountDownLatch tx2ReadDone = new CountDownLatch(1);

        AtomicReference<Throwable> tx2Error = new AtomicReference<>();


        Thread tx2Thread = new Thread(() -> {
            TransactionTemplate txTemplate = new TransactionTemplate(txManager);
            try {
                tx1ReadDone.await();

                txTemplate.execute(status -> {

                    Course staleCourse = courseRepository.findById(courseId).orElseThrow();
                    tx2ReadDone.countDown();


                    staleCourse.increaseEnrolledCount();
                    courseRepository.save(staleCourse);
                    entityManager.flush();


                    enrollmentRepository.save(
                            Enrollment.builder()
                                    .user(userRepository.findById(user2Id).orElseThrow())
                                    .course(staleCourse)
                                    .build()
                    );
                    return null;
                });

            } catch (Exception e) {
                tx2Error.set(e);
                tx2ReadDone.countDown();
            }
        });

        tx2Thread.start();


        TransactionTemplate txTemplate = new TransactionTemplate(txManager);
        txTemplate.execute(status -> {

            Course freshCourse = courseRepository.findById(courseId).orElseThrow();
            tx1ReadDone.countDown();

            try {
                tx2ReadDone.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }


            freshCourse.increaseEnrolledCount();
            courseRepository.save(freshCourse);
            entityManager.flush();


            enrollmentRepository.save(
                    Enrollment.builder()
                            .user(userRepository.findById(user1Id).orElseThrow())
                            .course(freshCourse)
                            .build()
            );
            return null;
        });

        tx2Thread.join();


        long enrollmentCount = enrollmentRepository.findAllByCourseId(courseId).size();
        Course result = courseRepository.findById(courseId).orElseThrow();

        System.out.println("\n============================");
        System.out.println("  레이스 컨디션 결과");
        System.out.println("============================");
        System.out.println("정원(capacity)        : 1");
        System.out.println("DB 수강신청 레코드 수  : " + enrollmentCount);
        System.out.println("Course.enrolledCount  : " + result.getEnrolledCount());
        if (tx2Error.get() != null) {
            System.out.println("TX2 오류              : " + tx2Error.get().getMessage());
        }
        System.out.println("============================\n");

        // 정원=1 이지만 2개의 수강신청 레코드가 생성됨 → 레이스 컨디션 발생!
        assertThat(enrollmentCount)
                .as("정원=1 이지만 실제 수강신청 수=%d", enrollmentCount)
                .isGreaterThan(1);
    }
}

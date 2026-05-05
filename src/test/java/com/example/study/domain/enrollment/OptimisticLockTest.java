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
import jakarta.persistence.OptimisticLockException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class OptimisticLockTest {

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
    @DisplayName("[낙관적 락] TX2가 TX1보다 늦게 커밋하면 OptimisticLockingFailureException이 발생한다")
    void optimisticLockTest() throws InterruptedException {

        Course course = courseRepository.save(
                Course.builder().name("인기 강의").capacity(2).enrolledCount(0).build()
        );
        User user1 = userRepository.save(User.builder().name("user1").build());
        User user2 = userRepository.save(User.builder().name("user2").build());

        Long courseId = course.getId();
        Long user1Id  = user1.getId();
        Long user2Id  = user2.getId();


        CountDownLatch tx1ReadDone  = new CountDownLatch(1);
        CountDownLatch tx2ReadDone  = new CountDownLatch(1);
        //tx1의 flush가 완료됐다는 신호
        CountDownLatch tx1FlushDone = new CountDownLatch(1);

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


                    try { tx1FlushDone.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                    entityManager.flush();

                    enrollmentRepository.save(
                            Enrollment.builder()
                                    .user(userRepository.findById(user2Id).orElseThrow())
                                    .course(staleCourse)
                                    .build()
                    );
                    return null;
                });
            //flush이기 떄문에 가능한 예외처리
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
            tx1FlushDone.countDown();

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
        System.out.println("  낙관적 락 결과");
        System.out.println("============================");
        System.out.println("정원(capacity)        : 2");
        System.out.println("DB 수강신청 레코드 수  : " + enrollmentCount);
        System.out.println("Course.enrolledCount  : " + result.getEnrolledCount());
        System.out.println("Course.version        : " + result.getVersion());
        if (tx2Error.get() != null) {
            System.out.println("TX2 예외 타입          : " + tx2Error.get().getClass().getSimpleName());
            System.out.println("TX2 오류              : " + tx2Error.get().getMessage());
        }
        System.out.println("============================\n");


        assertThat(tx2Error.get())
                .as("TX2는 낙관적 락 예외를 던져야 한다")
                .isInstanceOf(OptimisticLockException.class);

        assertThat(enrollmentCount)
                .as("낙관적 락으로 정원 초과가 방지되어 수강신청은 1건이어야 한다")
                .isEqualTo(1);


        assertThat(result.getEnrolledCount())
                .as("enrolledCount는 1이어야 한다")
                .isEqualTo(1);
    }
}

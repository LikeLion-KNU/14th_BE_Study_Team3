package com.example.study.domain.enrollment;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.domain.enrollment.entity.Enrollment;
import com.example.study.domain.enrollment.repository.EnrollmentRepository;
import com.example.study.domain.user.User;
import com.example.study.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NPlusOneTest {

    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManagerFactory emf;
    @PersistenceContext private EntityManager entityManager;

    private Statistics stats;

    @BeforeEach
    void setUp() {
        // Hibernate 쿼리 통계 활성화
        stats = emf.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);

        // 테스트 데이터 생성 (5명의 유저, 5개의 강의, 5개의 수강신청)
        for (int i = 1; i <= 5; i++) {
            User user = userRepository.save(User.builder().name("user" + i).build());
            Course course = courseRepository.save(
                    Course.builder().name("강의" + i).capacity(30).enrolledCount(0).build()
            );
            enrollmentRepository.save(
                    Enrollment.builder().user(user).course(course).build()
            );
        }
    }

    @AfterEach
    void tearDown() {
        enrollmentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("[N+1 문제] enrollment 조회 후 course에 접근하면 N번의 추가 쿼리가 발생한다")
    void nPlusOneProblemTest() {
        entityManager.clear(); // 1차 캐시(L1 Cache) 초기화 - setUp에서 저장된 엔티티 제거
        stats.clear();

        // 1번 쿼리: SELECT * FROM enrollment
        List<Enrollment> enrollments = enrollmentRepository.findAll();


        for (Enrollment enrollment : enrollments) {
            String courseName = enrollment.getCourse().getName();
        }

        long queryCount = stats.getPrepareStatementCount();

        System.out.println("\n============================");
        System.out.println("  N+1 문제 결과");
        System.out.println("============================");
        System.out.println("enrollment 수       : " + enrollments.size());
        System.out.println("실행된 쿼리 수       : " + queryCount);
        System.out.println("예상: 1(enrollment 조회) + 5(course 각각 조회) = 6");
        System.out.println("============================\n");


        assertThat(queryCount)
                .as("N+1 문제로 인해 1 + N(%d)번의 쿼리가 실행되어야 한다", enrollments.size())
                .isEqualTo(1 + enrollments.size());
    }

    @Test
    @Transactional
    @DisplayName("[fetch join 해결] JOIN FETCH로 조회하면 쿼리 1번으로 모든 데이터를 가져온다")
    void fetchJoinSolutionTest() {
        entityManager.clear(); // 1차 캐시(L1 Cache) 초기화
        stats.clear();

        List<Enrollment> enrollments = enrollmentRepository.findAllWithFetchJoin();


        for (Enrollment enrollment : enrollments) {
            String courseName = enrollment.getCourse().getName();
            String userName = enrollment.getUser().getName();
        }

        long queryCount = stats.getPrepareStatementCount();

        System.out.println("\n============================");
        System.out.println("  fetch join 해결 결과");
        System.out.println("============================");
        System.out.println("enrollment 수       : " + enrollments.size());
        System.out.println("실행된 쿼리 수       : " + queryCount);
        System.out.println("예상: 1(fetch join으로 한 번에 조회)");
        System.out.println("============================\n");


        assertThat(queryCount)
                .as("fetch join으로 쿼리가 1번만 실행되어야 한다")
                .isEqualTo(1);
    }
}

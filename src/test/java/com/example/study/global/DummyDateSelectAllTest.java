package com.example.study.global;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.domain.user.User;
import com.example.study.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DummyDateSelectAllTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @DisplayName("saveAll을 이용한 대량 더미 데이터 삽입 성능 측정")
    void insertDummyData_performance_test() {
        // given
        int userCount = 30000;
        int courseCount = 5000;

        List<User> users = new ArrayList<>();

        for (int i = 1; i <= userCount; i++) {
            users.add(User.builder()
                    .name("User" + i)
                    .build()
            );
        }

        List<Course> courses = new ArrayList<>();
        for (int i = 1; i <= courseCount; i++) {
            courses.add(Course.builder()
                    .name("더미 강의 " + i)
                    .capacity(70)
                    .enrolledCount(0)
                    .build());
        }

        StopWatch stopWatch = new StopWatch("더미 데이터 삽입 성능 측정");

        // when, 유저 삽입 시간 측정
        stopWatch.start("유저 30,000명 saveAll");
        userRepository.saveAll(users);
        stopWatch.stop();

        // when, 강의 삽입 시간 측정
        stopWatch.start("강의 5,000개 saveAll");
        courseRepository.saveAll(courses);
        stopWatch.stop();

        // then
        System.out.println("=========================================");
        System.out.println(stopWatch.prettyPrint());
        System.out.println("총 걸린 시간: " + stopWatch.getTotalTimeSeconds() + "초");
        System.out.println("=========================================");
    }
}

package com.example.study.domain.course;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.util.StopWatch;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseJdbcRepository;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CourseJdbcRepository.class)
class CourseJdbcRepositoryPerformanceTest {
    @Autowired
    private CourseJdbcRepository courseJdbcRepository;
    
    private final StopWatch stopWatch = new StopWatch();

    @RepeatedTest(value = 20)
    @DisplayName("Measuring the time taken to perform a batch insert of 5000 sample course records")
    void testExcutionTimeWhenBatchInsertCourseDummyData(TestInfo testInfo) {
        // Given
        List<Course> courseList = new ArrayList<Course>();
        for (int i = 0; i < 5000; i++) {
            courseList.add(Course.builder()
                .name("course" + i)
                .enrolledCount(0)
                .capacity(10)
                .build()
            );
        }

        // When
        stopWatch.start();
        courseJdbcRepository.batchInsert(courseList);
        stopWatch.stop();

        // Than
        System.out.println("batch insert of 5000 sample course records(" + testInfo.getDisplayName() + ")" + " : " + stopWatch.getTotalTimeMillis() + " MS");
    }

}
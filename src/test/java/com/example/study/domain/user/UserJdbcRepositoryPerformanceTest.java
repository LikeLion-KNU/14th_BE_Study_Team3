package com.example.study.domain.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.util.StopWatch;

import com.example.study.domain.user.repository.UserJdbcRepository;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserJdbcRepository.class)
public class UserJdbcRepositoryPerformanceTest {
    @Autowired
    private UserJdbcRepository userJdbcRepository;
    
    private final StopWatch stopWatch = new StopWatch();
    
    @BeforeEach
    static void beforeEach() {
        System.out.println("----- start test to user jdbc repository performance -----");
    }

    @RepeatedTest(value = 20)
    @DisplayName("Measuring the time taken to perform a batch insert of 30000 sample user records")
    void testExcutionTimeWhenBatchInsertUserDummyData(TestInfo testInfo) {
        // Given
        List<User> userList = new ArrayList<User>();
        for (int i = 1; i <= 30000; i++) {
            userList.add(User.builder().name("user" + i).build());
        }

        // When
        stopWatch.start();
        userJdbcRepository.batchInsert(userList);
        stopWatch.stop();

        // Than
        System.out.println("batch insert of 30000 sample user records(" + testInfo.getDisplayName() + ")" + " : " + stopWatch.getTotalTimeMillis() + " MS");
    }
}
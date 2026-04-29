package com.example.study.domain.user;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import com.example.study.domain.user.repository.UserJdbcRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class UserJdbcRepositoryPerformanceTest {
    @Autowired
    private UserJdbcRepository userJdbcRepository;
    
    private final StopWatch stopWatch = new StopWatch();

    @Test
    @DisplayName("Measuring the time taken to perform a batch insert of 30000 sample user records")
    @Transactional
    void testExcutionTimeWhenBatchInsertUserDummyData() {
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
        System.out.println("batch insert of 30000 sample user records : " + stopWatch.getTotalTimeSeconds() + " seconds");
    }
}
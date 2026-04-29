package com.example.study.global;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional; // 추가됨
import org.springframework.test.annotation.Rollback;           // 추가됨
import org.springframework.util.StopWatch;

@SpringBootTest
public class ProcedureTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional // 네이티브 쿼리로 수정 작업을 할 때는 트랜잭션이 필수입니다.
    @Rollback(false) // 테스트 완료 후 데이터를 DB에 남기기 위해 추가 (스터디 확인용)
    @DisplayName("프로시저를 이용한 대용량 데이터 삽입 성능 측정")
    void measureProcedurePerformance() {
        StopWatch stopWatch = new StopWatch("프로시저 데이터 삽입 성능 측정");

        // [1] 유저 삽입 시간 측정
        stopWatch.start("유저 30,000명 프로시저");
        entityManager.createNativeQuery("CALL InsertDummyUsers()").executeUpdate();
        stopWatch.stop();

        // [2] 강의 삽입 시간 측정
        stopWatch.start("강의 5,000개 프로시저");
        entityManager.createNativeQuery("CALL InsertDummyCourses()").executeUpdate();
        stopWatch.stop();

        // 총 걸린 시간 출력
        System.out.println("=========================================");
        System.out.println(stopWatch.prettyPrint());
        System.out.println("총 걸린 시간: " + stopWatch.getTotalTimeSeconds() + "초");
        System.out.println("=========================================");
    }
}
package com.example.study.domain.course.redis;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.global.config.RedisTestCacheConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(RedisTestCacheConfig.class) // 테스트용 Redis 설정 임포트
public class RedisCacheTest {

    @Autowired
    private CourseServiceWithRedisCache redisService;

    @MockitoBean
    private CourseRepository courseRepository;

    @Test
    @DisplayName("Redis 분산 캐시가 적용되면 여러 번 조회해도 DB는 한 번만 호출된다")
    void testRedisCacheWorks() {
        // given: 0번 페이지 요청 시 더미 데이터 반환 설정
        PageRequest pageable = PageRequest.of(0, 20);
        Course dummyCourse = Course.builder().name("Redis 테스트 인기 과목").capacity(50).enrolledCount(10).build();
        Page<Course> dummyPage = new PageImpl<>(List.of(dummyCourse));

        given(courseRepository.findAll(pageable)).willReturn(dummyPage);

        // when: 동일한 페이지 번호로 3번 연속 조회
        System.out.println("=== 1. 첫 번째 조회 (DB 접근 및 Redis 저장) ===");
        redisService.findAllCourses(pageable);

        System.out.println("=== 2. 두 번째 조회 (Redis 캐시에서 반환) ===");
        redisService.findAllCourses(pageable);

        System.out.println("=== 3. 세 번째 조회 (Redis 캐시에서 반환) ===");
        redisService.findAllCourses(pageable);

        // then: Repository의 findAll 메서드는 단 1번만 실행되었음을 검증
        verify(courseRepository, times(1)).findAll(pageable);
    }
}
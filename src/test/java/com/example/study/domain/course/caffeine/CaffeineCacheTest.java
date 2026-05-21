package com.example.study.domain.course.caffeine;

import com.example.study.domain.course.entity.Course;
import com.example.study.domain.course.repository.CourseRepository;
import com.example.study.global.config.CaffeineTestCacheConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {CaffeineTestCacheConfig.class, CourseServiceWithCaffeineCache.class})
public class CaffeineCacheTest {

    @Autowired
    private CourseServiceWithCaffeineCache caffeineService;

    @MockitoBean
    private CourseRepository courseRepository;

    @Test
    @DisplayName("Caffeine 로컬 캐시가 적용되면 두 번째 조회부터는 DB를 호출하지 않는지 테스트")
    void testCaffeineCacheWorks() {
        // given
        PageRequest pageable = PageRequest.of(0, 20);
        Course dummyCourse = Course.builder().name("Caffeine 테스트 강의").capacity(30).enrolledCount(0).build();
        Page<Course> dummyPage = new PageImpl<>(List.of(dummyCourse));

        given(courseRepository.findAll(pageable)).willReturn(dummyPage);

        // when
        System.out.println("=== 1. 첫번째 조회 (DB 접근) ===");
        caffeineService.findAllCourses(pageable);

        System.out.println("=== 2. 두번째 조회 (캐시에서 즉시 반환) ===");
        caffeineService.findAllCourses(pageable);

        System.out.println("=== 3. 세번째 조회 (캐시에서 즉시 반환) ===");
        caffeineService.findAllCourses(pageable);

        // then
        // 서비스 메서드 3번 호출 시, 실제 Repository의 findAll은 단 1번만 실행되는지 검증
        verify(courseRepository, times(1)).findAll(pageable);
    }
}

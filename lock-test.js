import http from 'k6/http';
import { check, sleep } from 'k6';

// 테스트 옵션 설정
export let options = {
    stages: [
        { duration: '10s', target: 1000 }, // 10초 동안 0명에서 1000명으로 점진적 증가
        { duration: '30s', target: 1000 }, // 30초 동안 1000명 유지 (본격적인 부하)
        { duration: '10s', target: 0 },    // 10초 동안 0명으로 감소
    ],
};

export default function () {
    // 1. Redis 분산 락 적용 안됐을 때의 테스트
    const url = 'http://localhost:8080/api/enrollments';

    // 2. Redis 분산 락 적용 테스트
    // const url = 'http://localhost:8080/api/enrollments/distributed';

    // 1 ~ 30000 사이의 랜덤 유저 ID 생성
    const randomUserId = Math.floor(Math.random() * 30000) + 1;

    // 시나리오 A: 랜덤 강의 (트래픽 분산 테스트)
    // const targetCourseId = Math.floor(Math.random() * 5000) + 1;

    // 시나리오 B: 단일 인기 강의 (트래픽 집중 병목 테스트)
    const targetCourseId = 1;

    const payload = JSON.stringify({
        userId: randomUserId,
        courseId: targetCourseId
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // API 요청
    const res = http.post(url, payload, params);

    // 응답 검증
    check(res, {
        'is success': (r) => r.status === 201 || r.status === 200,
    });

    // 1초 대기
    sleep(1);
}
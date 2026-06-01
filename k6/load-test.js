import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 커스텀 메트릭
const errorRate = new Rate('error_rate');
const responseTrend = new Trend('response_time');

export const options = {
    stages: [
        { duration: '10s', target: 100 },   // 10초 동안 100명까지 증가
        { duration: '20s', target: 1000 },  // 20초 동안 1000명까지 증가
        { duration: '30s', target: 1000 },  // 30초 동안 1000명 유지 (핵심 구간)
        { duration: '10s', target: 0 },     // 10초 동안 0명으로 감소
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],   // 95%의 요청이 500ms 이내
        error_rate: ['rate<0.05'],           // 에러율 5% 미만
    },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // GET /api/courses?page=0&size=20 요청
    const response = http.get(`${BASE_URL}/api/courses?page=0&size=20`, {
        headers: { 'Content-Type': 'application/json' },
    });

    // 응답 검증
    const success = check(response, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    errorRate.add(!success);
    responseTrend.add(response.timings.duration);

    sleep(0.1); // 요청 간 0.1초 대기 (너무 공격적이지 않게)
}

export function handleSummary(data) {
    return {
        stdout: `
========================================
         k6 부하 테스트 결과
========================================
총 요청 수         : ${data.metrics.http_reqs.values.count}
평균 응답 시간     : ${Math.round(data.metrics.http_req_duration.values.avg)}ms
p95 응답 시간      : ${Math.round(data.metrics.http_req_duration.values['p(95)'])}ms
p99 응답 시간      : ${Math.round(data.metrics.http_req_duration.values['p(99)'])}ms
최대 응답 시간     : ${Math.round(data.metrics.http_req_duration.values.max)}ms
TPS (req/s)       : ${Math.round(data.metrics.http_reqs.values.rate)}
에러율             : ${(data.metrics.http_req_failed.values.rate * 100).toFixed(2)}%
========================================
`,
    };
}

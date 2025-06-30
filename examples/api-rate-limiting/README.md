
# API Rate Limiting 예제

이 예제는 Token Bucket 알고리즘을 사용한 API 호출량 제한 처리기의 다양한 사용 시나리오를 보여줍니다.

## 🎯 포함된 예제들

### 1. 기본 사용법
- 간단한 Rate Limiter 생성 및 사용
- 토큰 소비 결과 확인

### 2. 버스트 트래픽 처리
- 갑작스런 대량 요청 처리
- 토큰 리필 메커니즘 확인

### 3. 동시 사용자 시뮬레이션
- 멀티스레드 환경에서의 동작
- 스레드 안전성 검증

### 4. 다양한 API 엔드포인트별 제한
- 엔드포인트별 다른 제한 정책
- 실제 API 서비스 시뮬레이션

### 5. 실시간 모니터링
- 실시간 통계 정보 출력
- 성능 메트릭 확인

## 🛠 커스터마이징

각 예제의 설정값들을 수정하여 다른 시나리오를 테스트해볼 수 있습니다:

- 토큰 용량 (`capacity`)
- 리필 속도 (`tokensPerSecond`)
- 리필 주기 (`refillPeriod`)
- 초기 토큰 수 (`initialTokens`)



## 🚀 실행 방법
```shell
./gradlew :examples:api-rate-limiting:run
```

```shell
./gradlew :examples:api-rate-limiting:build java -jar examples/api-rate-limiting/build/libs/api-rate-limiting.jar
```

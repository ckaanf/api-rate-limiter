# API Rate Limiter

고성능, 확장 가능한 Java Rate Limiting 라이브러리

## 주요 특징

- **다양한 알고리즘 지원**: Token Bucket (현재), Sliding Window (계획)
- **유연한 저장소**: Memory (현재), Redis (계획)
- **스레드 안전**: 고성능 동시성 지원
- **타입 안전**: Fluent Builder 패턴
- **Spring Boot 지원**: Auto-configuration (계획)

## 빠른 시작

### 의존성 추가

```shell
gradle 
implementation 'io.github.ckaanf:api-rate-limiter-core:1.0.0' 
implementation 'io.github.yourusername:api-rate-limiter-token-bucket:1.0.0' 
implementation 'io.github.yourusername:api-rate-limiter-storage-memory:1.0.0'
``` 

### 기본 사용법

```java 
// 1. 간단한 사용 - 초당 10개 요청 허용 io.github.ckaanf.ratelimiter.core.RateLimiter limiter = RateLimiters.create( io.github.ckaanf.ratelimiter.core.RateLimiterConfig.tokenBucketPerSecond(;


``` 

### 고급 설정

```java 
// 2. 상세 설정 - Fluent Builder 사용
``` 

### 다양한 소비 패턴

```java 
``` 

### 상태 모니터링

```java 
``` 

## 고급 사용법

### Registry 패턴으로 다중 Limiter 관리

```java
```

### 팩토리 메서드 활용

## 테스트

``` bash
# 전체 테스트 실행
./gradlew test

# 벤치마크 테스트 실행 (선택적)
./gradlew test -Dbenchmark=true

# 특정 모듈 테스트
./gradlew :algorithms:token-bucket:test
```

## 📄 라이선스

MIT License - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

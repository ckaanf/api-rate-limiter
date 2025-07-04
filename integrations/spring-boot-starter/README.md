# API Rate Limiter를 위한 Spring Boot Starter

[![JitPack](https://jitpack.io/v/ckaanf/api-rate-limiter.svg)](https://jitpack.io/#ckaanf/api-rate-limiter)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.java.net/projects/jdk/17/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 개요

이 모듈은 Spring Boot 애플리케이션에서 API Rate Limiter 라이브러리를 위한 자동 설정을 제공합니다. 최소한의 설정으로 Spring Boot 프로젝트에 속도 제한 기능을 쉽게 통합할 수 있습니다. 이 라이브러리는 API 요청 속도를 제어하여 시스템 과부하를 방지하고, 서비스의 안정성을 높이는 데 목적이 있습니다.

## 시작하기

### 의존성 추가

`build.gradle` 또는 `pom.xml` 파일에 다음 의존성을 추가하세요:

#### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://jitpack.io")
}
dependencies {
    implementation("com.github.ckaanf:api-rate-limiter:integrations-spring-boot-starter:1.0.2")
}
```

#### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.ckaanf</groupId>
    <artifactId>api-rate-limiter</artifactId>
    <version>1.0.2</version>
    <classifier>integrations-spring-boot-starter</classifier>
</dependency>
```

### 설정

`application.properties` 또는 `application.yml` 파일에서 속도 제한기를 설정할 수 있습니다. 기본적으로 Rate Limiter는 활성화되어 있으며, 다양한 속성을 통해 동작을 커스터마이징할 수 있습니다:

#### application.properties
```properties
# Rate Limiter 활성화 여부
rate-limiter.enabled=true

# 기본 Rate Limiter 설정
rate-limiter.limiters[0].name=default-api
rate-limiter.limiters[0].algorithm.token-bucket.rate=10
rate-limiter.limiters[0].algorithm.token-bucket.capacity=100
rate-limiter.limiters[0].storage.type=in-memory

# 추가 Rate Limiter 설정 (필요한 경우)
rate-limiter.limiters[1].name=critical-api
rate-limiter.limiters[1].algorithm.token-bucket.rate=5
rate-limiter.limiters[1].algorithm.token-bucket.capacity=50
rate-limiter.limiters[1].storage.type=in-memory
```

#### application.yml
```yaml
rate-limiter:
  enabled: true
  limiters:
    - name: default-api
      algorithm:
        token-bucket:
          rate: 10
          capacity: 100
      storage:
        type: in-memory
    - name: critical-api
      algorithm:
        token-bucket:
          rate: 5
          capacity: 50
      storage:
        type: in-memory
```

### 주요 설정 속성

- `rate-limiter.enabled`: Rate Limiter 기능을 활성화하거나 비활성화합니다. (기본값: true)
- `rate-limiter.limiters`: 여러 Rate Limiter를 정의할 수 있는 배열입니다.
  - `name`: Rate Limiter의 고유 이름입니다. 애플리케이션 내에서 참조할 때 사용됩니다.
  - `algorithm.token-bucket.rate`: 초당 생성되는 토큰 수로, 요청 처리 속도를 결정합니다.
  - `algorithm.token-bucket.capacity`: 버킷에 저장할 수 있는 최대 토큰 수로, 버스트 요청을 처리할 수 있는 용량을 의미합니다.
  - `storage.type`: 상태를 저장하는 방식입니다. 현재는 `in-memory`만 지원됩니다.

## 사용법

### 1. 직접 RateLimiter 빈 사용

설정이 완료되면 `RateLimiter` 빈이 Spring 컨텍스트에 자동으로 제공됩니다. 서비스나 컨트롤러에 주입하여 사용할 수 있습니다:

```java
@RestController
@RequestMapping("/api")
public class MyController {

    private final RateLimiter rateLimiter;

    @Autowired
    public MyController(@Qualifier("default-api") RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/endpoint")
    public ResponseEntity<String> handleRequest() {
        if (rateLimiter.tryConsume()) {
            return ResponseEntity.ok("요청이 처리되었습니다");
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("속도 제한을 초과했습니다");
        }
    }
}
```

### 2. `@RateLimit` 어노테이션 사용 (권장)

더 편리하게, `@RateLimit` 어노테이션을 사용하여 메서드 레벨에서 속도 제한을 적용할 수 있습니다. 이 방식은 코드의 가독성을 높이고, 설정을 분리할 수 있어 유지보수가 용이합니다:

```java
@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/data")
    @RateLimit(limiterName = "default-api") // 기본 비용은 1입니다.
    public ResponseEntity<String> getPublicData() {
        return ResponseEntity.ok("일반 데이터 조회 성공 (비용: 1)");
    }

    @PostMapping("/users")
    @RateLimit(limiterName = "critical-api", cost = 10) // 높은 비용 작업
    public ResponseEntity<String> createUser() {
        return ResponseEntity.status(HttpStatus.CREATED).body("중요한 사용자 생성 성공 (비용: 10)");
    }

    @DeleteMapping("/users/{id}")
    @RateLimit(limiterName = "critical-api", cost = 5) // 중간 비용 작업
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok("사용자 삭제 성공 (비용: 5, ID: " + id + ")");
    }
}
```

#### `@RateLimit` 속성
- `limiterName`: 사용할 Rate Limiter의 이름입니다. `application.properties`에서 정의한 이름과 일치해야 합니다.
- `cost`: 이 요청이 소비할 토큰 수입니다. 기본값은 1이며, 더 많은 리소스를 사용하는 작업에 대해 더 높은 값을 설정할 수 있습니다.

### 3. 예외 처리

속도 제한을 초과하면 `RateLimitExceededException`이 발생합니다. 이를 적절히 처리하여 사용자에게 적합한 응답을 제공할 수 있습니다:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimitExceededException(RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("속도 제한을 초과했습니다. 잠시 후 다시 시도해주세요.");
    }
}
```

## 예제 프로젝트

실제 사용 예제를 확인하려면 프로젝트 내의 [Spring Boot Example](../examples/spring-boot-example)을 참조하세요. 이 예제는 다양한 Rate Limiter 설정과 `@RateLimit` 어노테이션을 사용한 엔드포인트들을 포함하고 있습니다. 주요 파일은 다음과 같습니다:

- `Application.java`: Spring Boot 애플리케이션의 메인 클래스
- `ApiController.java`: Rate Limiter가 적용된 API 엔드포인트들
- `application.properties`: Rate Limiter 설정

예제를 실행하는 방법:
```bash
./gradlew :examples:spring-boot-example:bootRun
```

실행 후 `http://localhost:8081/api/data`와 같은 엔드포인트를 호출하여 속도 제한 동작을 확인할 수 있습니다.

## 고급 설정

### 다중 Rate Limiter 사용

하나의 애플리케이션에서 서로 다른 설정을 가진 여러 Rate Limiter를 사용할 수 있습니다. 예를 들어, 일반 API와 중요한 API에 대해 서로 다른 제한을 설정할 수 있습니다. `application.properties`에서 여러 `limiters`를 정의하고, `@RateLimit` 어노테이션의 `limiterName` 속성을 통해 적절한 Rate Limiter를 참조하세요.

### 버스트 처리

`algorithm.token-bucket.capacity` 값을 높게 설정하면 짧은 시간 동안 많은 요청을 처리하는 버스트 트래픽을 허용할 수 있습니다. 이는 사용자 경험을 개선하면서도 시스템을 보호하는 데 유용합니다.

## 제한 사항 및 주의사항

- 현재는 `in-memory` 저장소만 지원되며, 분산 환경에서는 적합하지 않습니다. 향후 Redis와 같은 분산 저장소 지원이 추가될 예정입니다.
- Rate Limiter는 애플리케이션 레벨에서 동작하므로, 네트워크 레벨의 DDoS 공격 방어에는 적합하지 않습니다.

## 기여하기

이 프로젝트는 오픈 소스이며, 버그 리포트, 기능 제안, 풀 리퀘스트를 환영합니다. [GitHub 저장소](https://github.com/ckaanf/api-rate-limiter)에서 이슈를 생성하거나 코드를 제출해주세요.

## 라이선스

이 프로젝트는 [MIT License](https://opensource.org/licenses/MIT) 하에 배포됩니다. 
# API Rate Limiter


[![JitPack](https://jitpack.io/v/ckaanf/api-rate-limiter.svg)](https://jitpack.io/#ckaanf/api-rate-limiter)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://openjdk.java.net/projects/jdk/17/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 개요



### 현재 지원되는 기능

| 모듈 | 상태 | 설명 |
|------|------|------|
| **Core** | ✅ | 핵심 인터페이스와 Registry 패턴 |
| **Token Bucket** | ✅ | 토큰 버킷 알고리즘 (burst 지원) |
| **Memory Storage** | ✅ | 메모리 기반 저장소 (TTL, 자동 정리) |
| **Spring Boot Starter** | 🚧 | Auto-configuration (개발 중) |

## 🚀 빠른 시작

### 의존성 추가

#### JitPack 사용 (권장)
```kotlin 
repositories { 
    maven("https://jitpack.io") 
}
dependencies { 
    // 올인원 패키지 (모든 기본 모듈 포함) 
     implementation("com.github.ckaanf:api-rate-limiter:1.0.2")
    
// 또는 개별 모듈 선택
implementation("com.github.ckaanf:api-rate-limiter:core:1.0.2")
implementation("com.github.ckaanf:api-rate-limiter:algorithms-token-bucket:1.0.2")
implementation("com.github.ckaanf:api-rate-limiter:storage-inmemory:1.0.2")
}
``` 

#### Gradle (Groovy)
```groovy 
repositories 
        { 
            maven 
                    { url(https://jitpack.io)} 
        }
dependencies 
        { 
            implementation 'com.github.ckaanf:api-rate-limiter:1.0.2' 
        }
```

### 기본 사용법


- 작성 예정

## 🤝 기여하기

기여를 환영합니다! 다음 방법으로 참여하실 수 있습니다:

1. **이슈 리포트**: 버그나 개선 사항을 [Issues](https://github.com/ckaanf/api-rate-limiter/issues)에 등록
2. **새로운 알고리즘**: `RateLimiterProvider` 인터페이스를 구현
3. **새로운 저장소**: `StorageProvider` 인터페이스를 구현
4. **문서 개선**: README, Javadoc 개선
5. **테스트 추가**: 엣지 케이스 테스트 추가

### 개발 환경 설정

```bash
git clone https://github.com/ckaanf/api-rate-limiter.git
cd api-rate-limiter
./gradlew build
```

## 📄 라이선스
이 프로젝트는 [MIT License](LICENSE) 하에 배포됩니다.
## 🔗 링크
- **JitPack**: [https://jitpack.io/#ckaanf/api-rate-limiter](https://jitpack.io/#ckaanf/api-rate-limiter)
- **이슈 트래커**: [https://github.com/ckaanf/api-rate-limiter/issues](https://github.com/ckaanf/api-rate-limiter/issues)
- **릴리즈 노트**: [https://github.com/ckaanf/api-rate-limiter/releases](https://github.com/ckaanf/api-rate-limiter/releases)



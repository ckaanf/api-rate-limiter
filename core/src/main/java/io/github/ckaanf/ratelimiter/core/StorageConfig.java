package io.github.ckaanf.ratelimiter.core;

import java.util.Map;

/**
 * 저장소 설정의 기본 인터페이스
 * 각 저장소 모듈에서 구체적인 구현을 제공
 */
public interface StorageConfig {

    /**
     * 저장소 타입 식별자
     *
     * @return 저장소 타입 (예: "memory", "redis", "database")
     */
    String getType();

    /**
     * 설정 유효성 검증
     *
     * @throws IllegalArgumentException 설정이 유효하지 않은 경우
     */
    void validate();

    /**
     * 연결 설정 정보 (Redis URL, DB 연결 정보 등)
     */
    Map<String, Object> getConnectionProperties();

    /**
     * 성능 관련 설정 (풀 크기, 타임아웃 등)
     */
    Map<String, Object> getPerformanceProperties();
}
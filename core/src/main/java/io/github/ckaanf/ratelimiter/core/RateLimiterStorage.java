package io.github.ckaanf.ratelimiter.core;

/**
 * Rate Limiter 저장소 추상화
 * 메모리, Redis, DB 등 다양한 저장소를 지원하기 위한 인터페이스
 */
public interface RateLimiterStorage {

    /**
     * 저장소 타입 식별자
     */
    String getType();

    /**
     * 토큰 소비 시도 (원자적 연산)
     *
     * @param context 소비 컨텍스트
     * @return 소비 결과
     */
    StorageResult tryConsume(StorageContext context);

    /**
     * 현재 토큰 상태 조회
     *
     * @param context 조회 컨텍스트
     * @return 토큰 상태
     */
    StorageResult getTokenState(StorageContext context);

    /**
     * 저장소 초기화
     *
     * @param config Rate Limiter 설정
     */
    void initialize(RateLimiterConfig config);

    /**
     * 저장소 정리 (만료된 데이터 제거 등)
     */
    void cleanup();

    /**
     * 저장소 종료
     */
    void shutdown();
}
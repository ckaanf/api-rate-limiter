import java.util.Map;

/**
 * 알고리즘 설정의 기본 인터페이스
 * 각 알고리즘 모듈에서 구체적인 구현을 제공
 */
public interface AlgorithmConfig {

    /**
     * 알고리즘 타입 식별자
     *
     * @return 알고리즘 타입 (예: "token-bucket", "sliding-window")
     */
    String getType();

    /**
     * 설정 유효성 검증
     *
     * @throws IllegalArgumentException 설정이 유효하지 않은 경우
     */
    void validate();

    /**
     * 설정을 Map으로 직렬화 (저장소 저장용)
     */
    Map<String, Object> toMap();

    /**
     * Map에서 설정 복원 (저장소 복원용)
     */
    static AlgorithmConfig fromMap(String type, Map<String, Object> map) {
        // SPI를 통해 각 알고리즘 모듈에서 구현
        throw new UnsupportedOperationException("Must be implemented by algorithm providers");
    }
}
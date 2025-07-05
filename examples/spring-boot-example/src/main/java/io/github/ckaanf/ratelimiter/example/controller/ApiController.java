package io.github.ckaanf.ratelimiter.example.controller;

import io.github.ckaanf.ratelimiter.springboot.starter.RateLimit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/data")
    @RateLimit(limiterName = "default-api") // cost의 기본값은 1입니다.
    public ResponseEntity<String> getPublicData() {
        return ResponseEntity.ok("일반 데이터 조회 성공 (비용: 1)");
    }

    @PostMapping("/users")
    @RateLimit(limiterName = "critical-api", cost = 10) // 이 요청은 비용이 높은 작업으로, 토큰 10개를 소비합니다.
    public ResponseEntity<String> createUser() {
        return ResponseEntity.status(HttpStatus.CREATED).body("중요한 사용자 생성 성공 (비용: 10)");
    }

    @DeleteMapping("/users/{id}")
    @RateLimit(limiterName = "critical-api", cost = 5) // 이 요청은 중간 정도 비용의 작업으로, 토큰 5개를 소비합니다.
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok("사용자 삭제 성공 (비용: 5, ID: " + id + ")");
    }
}
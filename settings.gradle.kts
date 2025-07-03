rootProject.name = "api-rate-limiter"

// 프로젝트 포함
include("core")
include("algorithms:token-bucket")
include("storage:inmemory")
include("storage:redis")
include("integration:spring-boot-starter")
include("examples:spring-boot-example")
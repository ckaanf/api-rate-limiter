rootProject.name = "api-rate-limiter"

include("core")

 include("algorithms:token-bucket")

 include("storage:inmemory")
// include("storage:redis")

 include("integrations:spring-boot-starter")

// 예제들 (나중에 추가)
// include("examples:spring-boot-example")
include("examples:api-rate-limiting")
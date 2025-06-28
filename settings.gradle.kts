rootProject.name = "api-rate-limiter"

include("core")

 include("algorithms:token-bucket")

 include("storage:memory")
// include("storage:redis")

 include("integrations:spring-boot-starter")
// 예제들 (나중에 추가)
// include("examples:basic-usage")
// include("examples:spring-boot-example")
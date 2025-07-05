dependencyResolutionManagement {
 repositories {
   mavenCentral()
   maven { url = uri("https://jitpack.io") }
 }
}

rootProject.name = "api-rate-limiter"

// 프로젝트 포함
include(":core")
include(":algorithms:token-bucket")
include(":storage:inmemory")
include(":storage:redis")
include(":integrations:spring-boot-starter")
include(":examples:api-rate-limiting")
include(":examples:spring-boot-example")
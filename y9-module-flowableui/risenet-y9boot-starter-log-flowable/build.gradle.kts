plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.lombok)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))

    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-configuration-processor")

    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-common-util")
    api(project(":y9-module-itemadmin:risenet-y9boot-api-interface-itemadmin"))

    api("org.springframework.kafka:spring-kafka")
    //api(libs.javax.ws.rs.api)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-starter-log-flowable"

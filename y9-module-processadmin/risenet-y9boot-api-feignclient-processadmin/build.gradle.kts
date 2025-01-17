plugins {
    id("net.risesoft.y9.conventions-java")
    id("net.risesoft.y9.lombok")
}

dependencies {
    api("net.risesoft:risenet-y9boot-starter-openfeign")
    api(project(":y9-module-processadmin:risenet-y9boot-api-interface-processadmin"))

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-api-feignclient-processadmin"

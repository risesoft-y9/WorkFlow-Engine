plugins {

    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.aspectj)
    alias(libs.plugins.y9.lombok)
    alias(libs.plugins.y9.smart.doc)
}

dependencies {
    api("net.risesoft:risenet-y9boot-starter-idgenerator")
    api("net.risesoft:risenet-y9boot-starter-kafka")
    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-common-model")
    api("net.risesoft:risenet-y9boot-support-file-jpa-repository")
    api("net.risesoft:risenet-y9boot-support-file-service-local")
    api("net.risesoft:risenet-y9boot-common-util")
    api("net.risesoft:risenet-y9boot-common-sqlddl")
    api("net.risesoft:risenet-y9boot-api-feignclient-platform")
    api("net.risesoft:risenet-y9boot-starter-multi-tenant")
    api(libs.flowable.spring.boot.starter.process)
    api(project(":y9-module-itemadmin:risenet-y9boot-api-feignclient-itemadmin"))
    api(project(":y9-module-processadmin:risenet-y9boot-api-interface-processadmin"))
    api("org.springframework:spring-webmvc")
    api(libs.fastjson)
    api(y9libs.google.guava)
    api(y9libs.httpcomponents.httpclient)
    // api(libs.jxl)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-processadmin"
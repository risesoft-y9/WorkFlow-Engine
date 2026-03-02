plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.aspectj)
    alias(libs.plugins.y9.lombok)
    alias(libs.plugins.y9.smart.doc)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))

    api(project(":y9-module-itemadmin:risenet-y9boot-support-itemadmin-jpa-repository"))
    api(project(":y9-module-itemadmin:risenet-y9boot-api-interface-itemadmin"))
    api(project(":y9-module-processadmin:risenet-y9boot-api-feignclient-processadmin"))
    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-common-model")
    api("net.risesoft:risenet-y9boot-common-util")
    api("net.risesoft:risenet-y9boot-common-sqlddl")
    api("net.risesoft:risenet-y9boot-starter-log")
    api("net.risesoft:risenet-y9boot-starter-jpa-public")
    api("net.risesoft:risenet-y9boot-starter-multi-tenant")
    api("net.risesoft:risenet-y9boot-starter-cache-redis")
    api("net.risesoft:risenet-y9boot-starter-listener-kafka")
    api("net.risesoft:risenet-y9boot-support-file-service-ftp")
    api("net.risesoft:risenet-y9boot-support-file-service-local")
    api("net.risesoft:risenet-y9boot-api-feignclient-platform")
    api("net.risesoft:risenet-y9boot-common-poi")

    api(y9libs.google.guava)
    api(libs.jodd.mail)
    // api(libs.fluent)
    // api(libs.jxl)
    api("org.hibernate.validator:hibernate-validator")

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-itemadmin"

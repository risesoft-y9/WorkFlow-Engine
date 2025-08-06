plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.lombok)
    alias(libs.plugins.y9.smart.doc)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))

    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-starter-jpa-public")
    api("net.risesoft:risenet-y9boot-starter-jpa-tenant")
    api("net.risesoft:risenet-y9boot-starter-log")
    api("net.risesoft:risenet-y9boot-support-file-service-ftp")
    api("net.risesoft:risenet-y9boot-support-file-service-local")
    api("net.risesoft:risenet-y9boot-common-sqlddl")
    api("net.risesoft:risenet-y9boot-api-feignclient-platform")
    api(project(":y9-module-flowableui:risenet-y9boot-starter-log-flowable"))
    api(project(":y9-module-itemadmin:risenet-y9boot-api-feignclient-itemadmin"))
    api(project(":y9-module-processadmin:risenet-y9boot-api-feignclient-processadmin"))
    api("net.risesoft:risenet-y9boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.apache.commons:commons-pool2")
    api(y9libs.google.guava)
    api(libs.pinyin4j)
    api(libs.poi.scratchpad)
    api(libs.poi.tl) {
        exclude(group = "xml-apis", module = "xml-apis")
    }
    api(libs.poi.ooxml)
    api(libs.fastexcel)
    api(libs.graph.java.client)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
}

description = "risenet-y9boot-support-flowableui"

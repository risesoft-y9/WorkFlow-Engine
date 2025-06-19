plugins {
    alias(libs.plugins.y9.conventions.war)
    alias(libs.plugins.y9.lombok)
    alias(libs.plugins.y9.docker)
    alias(libs.plugins.y9.smart.doc)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))
    providedRuntime(platform(libs.y9.digitalbase.dependencies))

    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-starter-security") {
        exclude(group = "xml-apis", module = "xml-apis")
    }
    api("net.risesoft:risenet-y9boot-starter-apisix")
    api("net.risesoft:risenet-y9boot-starter-log")
    api("net.risesoft:risenet-y9boot-starter-jpa-public")
    api("net.risesoft:risenet-y9boot-starter-jpa-tenant")
    api("net.risesoft:risenet-y9boot-support-file-service-ftp")
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-common-sqlddl")
    api("net.risesoft:risenet-y9boot-api-feignclient-platform")
    api(project(":y9-module-flowableui:risenet-y9boot-starter-log-flowable"))
    api(project(":y9-module-itemadmin:risenet-y9boot-api-feignclient-itemadmin"))
    api(project(":y9-module-processadmin:risenet-y9boot-api-feignclient-processadmin"))
    api("org.springframework.boot:spring-boot-starter-validation")
    api("net.risesoft:risenet-y9boot-starter-web")
    api("net.risesoft:risenet-y9boot-common-nacos")
    api("org.apache.commons:commons-pool2")
    api(y9libs.google.guava)
    api(y9libs.commons.codec)
    api(y9libs.commons.io)
    api(libs.pinyin4j)

    api(libs.poi.scratchpad)
    api(libs.poi.tl) {
        exclude(group = "xml-apis", module = "xml-apis")
    }
    api(libs.poi.ooxml)
    api(libs.fastexcel)
    api(y9libs.dom4j)
    api(libs.graph.java.client)
    // 条码号图片生成
    api(libs.jbarcode)
    api(libs.pdf417)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-flowableui"

val finalName = "flowableUI"
y9Docker {
    appName = finalName
}

y9War {
    archiveBaseName = finalName
}
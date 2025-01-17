plugins {
    id("net.risesoft.y9.conventions-war")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    id("net.risesoft.y9.smart-doc")
    alias(y9libs.plugins.org.springframework.boot)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))
    providedRuntime(platform(libs.y9.digitalbase.dependencies))

    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-starter-security")
    api("net.risesoft:risenet-y9boot-starter-apisix")
    api("net.risesoft:risenet-y9boot-starter-log")
    api("net.risesoft:risenet-y9boot-starter-jpa-public")
    api("net.risesoft:risenet-y9boot-starter-jpa-tenant")
    api("net.risesoft:risenet-y9boot-support-file-service-ftp")
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-common-sqlddl")
    api("net.risesoft:risenet-y9boot-api-feignclient-platform")
    api(project(":y9-module-itemadmin:risenet-y9boot-api-feignclient-itemadmin"))
    api(project(":y9-module-processadmin:risenet-y9boot-api-feignclient-processadmin"))
    api("org.springframework.boot:spring-boot-starter-validation")
    api("net.risesoft:risenet-y9boot-starter-web")
    api("net.risesoft:risenet-y9boot-common-nacos")
    api("org.apache.commons:commons-pool2")
    api(y9libs.google.guava)
    api(y9libs.commons.codec)
    api(y9libs.commons.io)
    api(libs.jxl)
    api(libs.pinyin4j)
    api(libs.com.baidu.ueditor)
    api(libs.thumbnailator)
    api(libs.poi.tl)
    api(libs.poi.ooxml)
    api(libs.poi.ooxml.schemas)
    api(libs.poi.scratchpad)
    api(libs.graph.java.client)

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-flowableui"

val finalName = "flowableUI"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}
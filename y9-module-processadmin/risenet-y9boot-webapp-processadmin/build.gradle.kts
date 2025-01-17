plugins {
    id("net.risesoft.y9.conventions-war")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    alias(y9libs.plugins.org.springframework.boot)
}

dependencies {
    providedRuntime(platform(libs.y9.digitalbase.dependencies))

    api(project(":y9-module-processadmin:risenet-y9boot-support-processadmin")) {
        // 排除项目依赖
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("log4j", "log4j")
        exclude("ch.qos.logback", "logback-core")
    }
    api("net.risesoft:risenet-y9boot-properties")
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-common-tenant-datasource")
    api("net.risesoft:risenet-y9boot-starter-security") {
        // 排除项目依赖 
        exclude("xml-apis", "xml-apis")
    }
    api("net.risesoft:risenet-y9boot-api-feignclient-platform")
    api("net.risesoft:risenet-y9boot-starter-web")
    api("net.risesoft:risenet-y9boot-common-nacos")
    api("org.springframework.boot:spring-boot-starter-validation") {
        // 排除项目依赖
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("ch.qos.logback", "logback-core")
        exclude("ch.qos.logback", "logback-classic")
    }
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-log4j2")
    api("org.apache.commons:commons-pool2")
    api(y9libs.mysql.connector.j)
    api(y9libs.dameng.dmdialect.hibernate62)
    api(y9libs.dameng.dmjdbcdriver18)
    api(libs.com.oracle.database.jdbc.ojdbc11)
    api(libs.kingbase.hibernate.dialect)
    api(libs.kingbase.hibernate.jpa.api)
    api(libs.kingbase.kingbase8)

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-processadmin"

val finalName = "processAdmin"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}
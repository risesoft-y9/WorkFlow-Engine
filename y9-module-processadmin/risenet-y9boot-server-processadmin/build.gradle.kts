plugins {
    alias(libs.plugins.y9.conventions.war)
    alias(libs.plugins.y9.lombok)
    alias(libs.plugins.y9.docker)
}

dependencies {
    providedRuntime(platform(libs.y9.digitalbase.dependencies))

    api(project(":y9-module-processadmin:risenet-y9boot-support-processadmin"))
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
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.apache.commons:commons-pool2")
    api(y9libs.mysql.connector.j)
    api(libs.dameng.dmdialect.hibernate66)
    api(libs.dameng.dmjdbcdriver11)
    api(libs.com.oracle.database.jdbc.ojdbc17)
    api(libs.kingbase.kingbase8)
    api(libs.kingbase.kesdialect.hibernate62)

    compileOnly("jakarta.servlet:jakarta.servlet-api")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-server-processadmin"

val finalName = "server-processadmin"
y9Docker {
    appName = finalName
}

y9War {
    archiveBaseName = finalName
}

// 跳过 Maven 发布（类似 Maven 的 <maven.deploy.skip>true</maven.deploy.skip>）
tasks.withType<PublishToMavenRepository> {
    enabled = false
}
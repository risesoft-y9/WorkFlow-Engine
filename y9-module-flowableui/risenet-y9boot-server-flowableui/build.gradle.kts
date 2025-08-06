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

    api(project(":y9-module-flowableui:risenet-y9boot-support-flowableui"))
    api("net.risesoft:risenet-y9boot-starter-security") {
        exclude(group = "xml-apis", module = "xml-apis")
    }
    api("net.risesoft:risenet-y9boot-starter-apisix")
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-common-nacos")

    compileOnly("jakarta.servlet:jakarta.servlet-api")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-server-flowableui"

val finalName = "server-flowableui"
y9Docker {
    appName = finalName
}

y9War {
    archiveBaseName = finalName
}
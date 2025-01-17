plugins {
    id("net.risesoft.y9.conventions-war")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    alias(y9libs.plugins.org.springframework.boot)
}

dependencies {
    providedRuntime(platform(libs.y9.digitalbase.dependencies))

    api(project(":y9-module-itemadmin:risenet-y9boot-support-itemadmin"))
    api("net.risesoft:risenet-y9boot-starter-sso-oauth2-resource")
    api("net.risesoft:risenet-y9boot-starter-security")
    api(y9libs.httpcomponents.httpmime)
    api("org.springframework.boot:spring-boot-starter-validation")
    api("net.risesoft:risenet-y9boot-starter-multi-tenant")
    api("net.risesoft:risenet-y9boot-starter-web")
    api("net.risesoft:risenet-y9boot-common-nacos")
    api("org.apache.commons:commons-pool2")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-itemadmin"

val finalName = "itemAdmin"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}
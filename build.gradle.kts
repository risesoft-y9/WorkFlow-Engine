plugins {
    java
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
    id("tech.yanand.maven-central-publish") version "1.3.0" apply false

    alias(libs.plugins.y9.aspectj) apply false
    alias(libs.plugins.y9.docker) apply false
    alias(libs.plugins.y9.conventions.java) apply false
    alias(libs.plugins.y9.conventions.war) apply false
    alias(libs.plugins.y9.java.publish) apply false
    alias(libs.plugins.y9.java.publish.central) apply false
    alias(libs.plugins.y9.javaPlatform.publish) apply false
    alias(libs.plugins.y9.javaPlatform.publish.central) apply false
    alias(libs.plugins.y9.lombok) apply false
    alias(libs.plugins.y9.repository) apply false
    alias(libs.plugins.y9.smart.doc) apply false
}

repositories {
    mavenCentral()
    //gradlePluginPortal()
}
dependencies {
    //management(platform(project(":y9-digitalbase-dependencies")))
    //management(platform(libs.spring.boot.bom))
}

group = "net.risesoft"
version = findProperty("Y9_VERSION") as String? ?: "9.7.0-SNAPSHOT"

rootProject.subprojects.forEach { p ->
    p.version = findProperty("Y9_VERSION") as String? ?: "9.7.0-SNAPSHOT"
    p.extra.set("Y9_VERSION", version)
}
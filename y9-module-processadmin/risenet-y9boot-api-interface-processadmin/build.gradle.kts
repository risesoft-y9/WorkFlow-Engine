plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.lombok)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))

    api("net.risesoft:risenet-y9boot-common-model")
    api("org.springframework:spring-web")
}

description = "risenet-y9boot-api-interface-processadmin"

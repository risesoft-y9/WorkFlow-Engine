plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.lombok)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))
    api("net.risesoft:risenet-y9boot-common-platform")
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("com.alibaba:transmittable-thread-local")
}

description = "risenet-y9boot-model-itemadmin"

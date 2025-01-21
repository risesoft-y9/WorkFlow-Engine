plugins {
    alias(libs.plugins.y9.conventions.java)
    alias(libs.plugins.y9.lombok)
}

dependencies {
    api("net.risesoft:risenet-y9boot-starter-openfeign")
    api(project(":y9-module-itemadmin:risenet-y9boot-api-interface-itemadmin"))
}

description = "risenet-y9boot-api-feignclient-itemadmin"

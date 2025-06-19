pluginManagement {
    // Include 'plugins build' to define convention plugins.
    //includeBuild("build-logic")

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}


dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    //引入y9的版本定义
    versionCatalogs {
        create("y9libs") {
            from("net.risesoft.y9:risenet-gradle-version-catalog:9.7.0-03")
        }
    }
}

rootProject.name = "y9-flowable"
include(":y9-module-processadmin:risenet-y9boot-api-interface-processadmin")
include(":y9-module-processadmin:risenet-y9boot-api-feignclient-processadmin")
include(":y9-module-processadmin:risenet-y9boot-support-processadmin")
include(":y9-module-processadmin:risenet-y9boot-webapp-processadmin")

include(":y9-module-itemadmin:risenet-y9boot-api-interface-itemadmin")
include(":y9-module-itemadmin:risenet-y9boot-api-feignclient-itemadmin")
include(":y9-module-itemadmin:risenet-y9boot-support-itemadmin-jpa-repository")
include(":y9-module-itemadmin:risenet-y9boot-support-itemadmin")
include(":y9-module-itemadmin:risenet-y9boot-webapp-itemadmin")

include(":y9-module-flowableui:risenet-y9boot-starter-log-flowable")
include(":y9-module-flowableui:risenet-y9boot-webapp-flowableui")
include(":y9-module-jodconverter:risenet-y9boot-webapp-jodconverter")


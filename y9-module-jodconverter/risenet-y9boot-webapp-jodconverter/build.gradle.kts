plugins {
    id("net.risesoft.y9.conventions-war")
    id("net.risesoft.y9.lombok")
    id("net.risesoft.y9.docker")
    alias(y9libs.plugins.org.springframework.boot)
}

dependencies {
    api(platform(libs.y9.digitalbase.bom))
    api(platform(libs.y9.digitalbase.dependencies))
    providedRuntime(platform(libs.y9.digitalbase.dependencies))

    api(libs.hutool.http)
    api(libs.jodconverter.local)
    api("net.risesoft:risenet-y9boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-freemarker")
    api(libs.poi)
    api(libs.poi.scratchpad)
    api(libs.poi.ooxml)
    api(libs.fr.opensagres.xdocreport.document)
    api(libs.org.apache.poi.xwpf.converter.core) {
        // 排除项目依赖
        exclude("org.apache.poi", "poi")
    }
    api(libs.org.apache.poi.xwpf.converter.xhtml)
    api(libs.sevenzipjbinding)
    api(libs.sevenzipjbinding.all.platforms)
    api("org.apache.commons:commons-lang3")
    api(libs.redisson)
    api(libs.juniversalchardet)
    api(libs.junrar)
    api(libs.jchardet)
    api(libs.antlr)
    api(libs.commons.cli)
    api(libs.commons.net)
    api(libs.xstream)
    api(libs.concurrentlinkedhashmap.lru)
    api(libs.rocksdbjni)
    api(libs.pdfbox)
    api(libs.pdfbox.tools)
    api(libs.pdfbox.jbig2.imageio)
    api(libs.jai.imageio.jpeg2000)
    api(libs.jai.imageio.core)
    api(libs.aspose.cad)
    api(libs.bcprov.jdk15on)
    api(libs.galimatias)
    api(libs.org.bytedeco.javacv)
    api(libs.org.bytedeco.javacpp)
    implementation(libs.org.bytedeco.opencv) {
        artifact {
            classifier = 'linux-x86_64'
        }
    }
    implementation(libs.org.bytedeco.opencv)
    {
        artifact {
            classifier = 'windows-x86_64'
        }
    }
    implementation(libs.org.bytedeco.openblas) {
        artifact {
            classifier = 'linux-x86_64'
        }
    }
    implementation(libs.org.bytedeco.openblas) {
        artifact {
            classifier = 'windows-x86_64'
        }
    }
    implementation(libs.org.bytedeco.ffmpeg) {
        artifact {
            classifier = 'linux-x86_64'
        }
    }
    implementation(libs.org.bytedeco.ffmpeg) {
        artifact {
            classifier = 'windows-x86_64'
        }
    }
    api(libs.itextpdf)
    api(libs.jai.codec)
    api(libs.jai.core)
    api("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(y9libs.commons.httpclient) {
        // 排除项目依赖
        exclude("commons-logging", "commons-logging")
    }

    providedRuntime("org.apache.tomcat.embed:tomcat-embed-jasper")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
}

description = "risenet-y9boot-webapp-jodconverter"

val finalName = "jodconverter"
jib.container.appRoot = "/usr/local/tomcat/webapps/${finalName}"

tasks.bootWar {
    archiveFileName.set("${finalName}.${archiveExtension.get()}")
}
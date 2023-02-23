plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    id("maven-publish")
    id("signing")
}

extra[EXT_POM_NAME] = "Ruler common"
extra[EXT_POM_DESCRIPTION] = "Common code used by the Ruler Gradle plugin & Ruler CLI"

java {
    withSourcesJar()

    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

dependencies {
    compileOnly(Dependencies.BUNDLETOOL)
    compileOnly(Dependencies.PROTOBUF_CORE)
    compileOnly(Dependencies.ANDROID_GRADLE_PLUGIN)
    compileOnly(Dependencies.ANDROID_TOOLS_COMMON)
    compileOnly(Dependencies.ANDROID_TOOLS_SDKLIB)
    compileOnly(Dependencies.DEXLIB)

    implementation(project(":ruler-models"))

    implementation(Dependencies.ANDROID_GRADLE_PLUGIN)
    implementation(Dependencies.APK_ANALYZER) {
        exclude(group = "com.android.tools.lint") // Avoid leaking incompatible Lint versions to consumers
    }
    implementation(Dependencies.KOTLINX_SERIALIZATION_JSON)
    implementation(Dependencies.SNAKE_YAML)

    testRuntimeOnly(Dependencies.JUNIT_ENGINE)
    testImplementation(gradleTestKit())
    testImplementation(Dependencies.JUNIT_API)
    testImplementation(Dependencies.JUNIT_PARAMS)
    testImplementation(Dependencies.GOOGLE_TRUTH)
    testImplementation(Dependencies.GOOGLE_GUAVA)
}

publishing {
    publications {
        create<MavenPublication>("jvm") {
            from(components["java"])
        }
    }
    configurePublications(project)
}

signing {
    configureSigning(publishing.publications)
}

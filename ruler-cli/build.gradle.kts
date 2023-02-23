plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    id("com.github.johnrengelman.shadow")
}

java {
    withSourcesJar()

    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}


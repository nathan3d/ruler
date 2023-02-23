plugins {
    id("application")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
    id("com.github.johnrengelman.shadow")
}

java {
    withSourcesJar()

    toolchain.languageVersion.set(JavaLanguageVersion.of(11))
}

dependencies {
    implementation(project(":ruler-models"))
    implementation(project(":ruler-common"))
    implementation("com.github.ajalt.clikt:clikt:3.5.1")
}

application {
    mainClass.set("com.spotify.ruler.cli.RulerCliKt")
}

@file:OptIn(ExperimentalSerializationApi::class)

package com.spotify.ruler.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.spotify.rulercommon.BaseRulerTask
import com.spotify.rulercommon.dependency.*
import com.spotify.rulercommon.models.RulerConfig
import com.spotify.rulercommon.models.AppInfo
import com.spotify.rulercommon.models.DeviceSpec
import com.spotify.rulercommon.sanitizer.ClassNameSanitizer
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class RulerCli: CliktCommand(), BaseRulerTask {
    val dependencyMap by argument().file()
    val rulerConfigJson by argument().file()

    override fun print(content: String) = echo(content)

    override fun provideMappingFile(): File? {
        return null
        TODO("Not yet implemented")
    }

    override fun provideResourceMappingFile(): File? {
        return null
        TODO("Not yet implemented")
    }

    override fun provideOwnershipFile(): File? {
        return null
        TODO("Not yet implemented")
    }

    override fun rulerConfig(): RulerConfig {
        val json = Json.decodeFromStream<JsonRulerConfig>(rulerConfigJson.inputStream())
        return RulerConfig(
            projectPath = json.projectPath,
            rootDir = File(json.rootDir),
            workingDir = File(json.workingDir),
            bundleFile = File(json.bundleFile),
            reportDir = File(json.reportDir),
            appInfo = json.appInfo,
            deviceSpec = json.deviceSpec,
            defaultOwner = json.defaultOwner,
            omitFileBreakdown = json.omitFileBreakdown)
    }

    override fun providesDependencies(): Map<String, List<DependencyComponent>> {
        val json = Json.decodeFromStream<ModuleMap>(dependencyMap.inputStream())

        val jarDependencies = json.jars.flatMap {
            JarArtifactParser().parseFile(ArtifactResult.JarArtifact(
                File(rulerConfig().workingDir, it.jar), it.module
            ))
        }

        val assets = json.assets.map {
            DependencyEntry.Default(it.filename, it.module)
        }

        val resources = json.resources.map {
            DependencyEntry.Default(it.filename, it.module)
        }

        val entries = jarDependencies + assets + resources

        val classNameSanitizer = ClassNameSanitizer(provideMappingFile())
        val dependencySanitizer = DependencySanitizer(classNameSanitizer)
        return dependencySanitizer.sanitize(entries)
    }

    override fun run() {
        super.run()
    }
}

@Serializable
data class JsonRulerConfig(
    val projectPath: String,
    val rootDir: String,
    val workingDir: String,
    val bundleFile: String,
    val reportDir: String,
    val appInfo: AppInfo,
    val deviceSpec: DeviceSpec,
    val defaultOwner: String,
    val omitFileBreakdown: Boolean
)

@Serializable
data class ModuleMap (
    val assets: List<Asset>,
    val jars: List<Jar>,
    val resources: List<Asset>
)

@Serializable
data class Asset (
    val filename: String,
    val module: String
)

@Serializable
data class Jar (
    val jar: String,
    val module: String
)

fun main(args: Array<String>) = RulerCli().main(args)

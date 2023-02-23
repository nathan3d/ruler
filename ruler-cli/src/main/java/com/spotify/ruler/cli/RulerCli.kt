@file:OptIn(ExperimentalSerializationApi::class)

package com.spotify.ruler.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.spotify.rulercommon.BaseRulerTask
import com.spotify.rulercommon.models.RulerConfig
import com.spotify.rulercommon.dependency.DependencyComponent
import com.spotify.rulercommon.models.AppInfo
import com.spotify.rulercommon.models.DeviceSpec
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class RulerCli: CliktCommand(), BaseRulerTask {
    val appBundle by argument().file()
    val dependencyMap by argument().file()
    val rulerConfigJson by argument().file()

    override fun print(content: String) = echo(content)

    override fun provideMappingFile(): File? {
        TODO("Not yet implemented")
    }

    override fun provideResourceMappingFile(): File? {
        TODO("Not yet implemented")
    }

    override fun provideOwnershipFile(): File? {
        TODO("Not yet implemented")
    }

    override val rulerConfig: RulerConfig
        get() {
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
                omitFileBreakdown = json.omitFileBreakdown
            )
        }
    override fun getDependencies(): Map<String, List<DependencyComponent>> {
        TODO("Not yet implemented")
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

fun main(args: Array<String>) = RulerCli().main(args)

/*
* Copyright 2021 Spotify AB
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.spotify.ruler.plugin

import com.spotify.ruler.plugin.dependency.EntryParser
import com.spotify.rulercommon.BaseRulerTask
import com.spotify.rulercommon.models.RulerConfig
import com.spotify.rulercommon.dependency.DependencyComponent
import com.spotify.rulercommon.dependency.DependencySanitizer
import com.spotify.rulercommon.models.AppInfo
import com.spotify.rulercommon.models.DeviceSpec
import com.spotify.rulercommon.sanitizer.ClassNameSanitizer
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import java.io.File

abstract class RulerTask : DefaultTask(), BaseRulerTask {

    @get:Input
    abstract val appInfo: Property<AppInfo>

    @get:Input
    abstract val deviceSpec: Property<DeviceSpec>

    @get:InputFile
    abstract val bundleFile: RegularFileProperty

    @get:Optional
    @get:InputFile
    abstract val mappingFile: RegularFileProperty

    @get:Optional
    @get:InputFile
    abstract val resourceMappingFile: RegularFileProperty

    @get:Optional
    @get:InputFile
    abstract val ownershipFile: RegularFileProperty

    @get:Input
    abstract val defaultOwner: Property<String>

    @get:Input
    abstract val omitFileBreakdown: Property<Boolean>

    @get:OutputDirectory
    abstract val workingDir: DirectoryProperty

    @get:OutputDirectory
    abstract val reportDir: DirectoryProperty

    @TaskAction
    fun analyze() {
        run()
    }
     override fun getDependencies(): Map<String, List<DependencyComponent>> {
        val dependencyParser = EntryParser()
        val entries = dependencyParser.parse(project, rulerConfig.appInfo)

        val classNameSanitizer = ClassNameSanitizer(provideMappingFile())
        val dependencySanitizer = DependencySanitizer(classNameSanitizer)
        return dependencySanitizer.sanitize(entries)
    }

    override fun print(content: String) = project.logger.lifecycle(content)
    override fun provideMappingFile(): File? = mappingFile.asFile.orNull
    override fun provideResourceMappingFile(): File? = resourceMappingFile.asFile.orNull
    override fun provideOwnershipFile(): File? = ownershipFile.asFile.orNull

    override val rulerConfig: RulerConfig
        get() = RulerConfig(
            projectPath = project.path,
            rootDir = project.rootDir,
            bundleFile = bundleFile.asFile.get(),
            workingDir = workingDir.asFile.get(),
            reportDir = reportDir.asFile.get(),
            appInfo = appInfo.get(),
            deviceSpec = deviceSpec.get(),
            defaultOwner = defaultOwner.get(),
            omitFileBreakdown = omitFileBreakdown.get()
        )
}

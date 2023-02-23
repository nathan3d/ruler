package com.spotify.rulercommon

import com.android.build.gradle.internal.SdkLocator
import com.android.builder.errors.DefaultIssueReporter
import com.android.utils.StdLogger
import com.spotify.ruler.models.AppFile
import com.spotify.ruler.models.ComponentType
import com.spotify.rulercommon.apk.ApkCreator
import com.spotify.rulercommon.apk.ApkParser
import com.spotify.rulercommon.apk.ApkSanitizer
import com.spotify.rulercommon.attribution.Attributor
import com.spotify.rulercommon.dependency.DependencyComponent
import com.spotify.rulercommon.models.RulerConfig
import com.spotify.rulercommon.ownership.OwnershipFileParser
import com.spotify.rulercommon.ownership.OwnershipInfo
import com.spotify.rulercommon.report.HtmlReporter
import com.spotify.rulercommon.report.JsonReporter
import com.spotify.rulercommon.sanitizer.ClassNameSanitizer
import com.spotify.rulercommon.sanitizer.ResourceNameSanitizer
import java.io.File
import java.nio.file.Path

interface BaseRulerTask {

    fun print(content: String)
    fun provideMappingFile(): File?
    fun provideResourceMappingFile(): File?
    fun provideOwnershipFile(): File?

    //    fun provideOmitFileBreakdown(): Boolean
    fun rulerConfig(): RulerConfig
    private val rulerConfig: RulerConfig
        get() = rulerConfig()

    fun providesDependencies(): Map<String, List<DependencyComponent>>

    fun run() {
        val files = getFilesFromBundle() // Get all relevant files from the provided bundle
        val dependencies = providesDependencies() // Get all entries from all dependencies

        // Split main APK bundle entries and dynamic feature module entries
        val mainFiles = files.getValue(ApkCreator.BASE_FEATURE_NAME)
        val featureFiles = files.filter { (feature, _) -> feature != ApkCreator.BASE_FEATURE_NAME }

        // Attribute main APK bundle entries and group into components
        val attributor =
            Attributor(DependencyComponent(rulerConfig.projectPath, ComponentType.INTERNAL))
        val components = attributor.attribute(mainFiles, dependencies)

        val ownershipInfo = getOwnershipInfo() // Get ownership information for all components
        generateReports(components, featureFiles, ownershipInfo)
    }

    private fun getFilesFromBundle(): Map<String, List<AppFile>> {
        val apkCreator = ApkCreator(
            rulerConfig.rootDir,
            getAndroidSdkLocation(rulerConfig.rootDir)
        )
        val splits = if (rulerConfig.bundleFile.extension == "apk") {
            mapOf("apk" to listOf(rulerConfig.bundleFile))
        } else {
            apkCreator.createSplitApks(
                rulerConfig.bundleFile,
                rulerConfig.deviceSpec,
                rulerConfig.workingDir
            )
        }

        val apkParser = ApkParser()
        val classNameSanitizer = ClassNameSanitizer(provideMappingFile())
        val resourceNameSanitizer = ResourceNameSanitizer(provideResourceMappingFile())
        val apkSanitizer = ApkSanitizer(classNameSanitizer, resourceNameSanitizer)

        return splits.mapValues { (_, apks) ->
            val entries = apks.flatMap(apkParser::parse)
            apkSanitizer.sanitize(entries)
        }
    }

    private fun getOwnershipInfo(): OwnershipInfo? {
        val ownershipFile = provideOwnershipFile() ?: return null
        val ownershipFileParser = OwnershipFileParser()
        val ownershipEntries = ownershipFileParser.parse(ownershipFile)

        return OwnershipInfo(ownershipEntries, rulerConfig.defaultOwner)
    }

    private fun generateReports(
        components: Map<DependencyComponent, List<AppFile>>,
        features: Map<String, List<AppFile>>,
        ownershipInfo: OwnershipInfo?,
    ) {
        val reportDir = rulerConfig.reportDir

        val jsonReporter = JsonReporter()
        val jsonReport = jsonReporter.generateReport(
            rulerConfig.appInfo,
            components,
            features,
            ownershipInfo,
            reportDir,
            rulerConfig.omitFileBreakdown
        )

        print("Wrote JSON report to ${jsonReport.toPath().toUri()}")

        val htmlReporter = HtmlReporter()
        val htmlReport = htmlReporter.generateReport(jsonReport.readText(), reportDir)
        print("Wrote HTML report to ${htmlReport.toPath().toUri()}")
    }


    /** Finds and returns the location of the Android SDK. */
    private fun getAndroidSdkLocation(rootDir: File): Path {
        val logger = StdLogger(StdLogger.Level.WARNING)
        val issueReporter = DefaultIssueReporter(logger)
        return SdkLocator.getSdkDirectory(rootDir, issueReporter).toPath()
    }
}



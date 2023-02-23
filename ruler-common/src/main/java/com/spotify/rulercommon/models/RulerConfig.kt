package com.spotify.rulercommon.models

import java.io.File

data class RulerConfig(
    val projectPath: String,
    val rootDir: File,
    val workingDir: File,
    val bundleFile: File,
    val reportDir: File,
    val appInfo: AppInfo,
    val deviceSpec: DeviceSpec,
    val defaultOwner: String,
    val omitFileBreakdown: Boolean
)
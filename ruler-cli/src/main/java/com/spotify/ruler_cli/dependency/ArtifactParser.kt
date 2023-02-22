package com.spotify.ruler_cli.dependency

import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarFile

abstract class ArtifactParser<in T> {
    abstract fun parseFile(artifactResult: T): List<DependencyEntry>
}

/** Plain artifact parser which returns a list of all artifact files. */
class DefaultArtifactParser : ArtifactParser<ArtifactResult.DefaultArtifact>() {

    override fun parseFile(artifact: ArtifactResult.DefaultArtifact): List<DependencyEntry> {
        val name =
            artifact.file.absolutePath.removePrefix(artifact.resolvedArtifactFile.absolutePath)
        return listOf(DependencyEntry.Default(name, artifact.component))
    }
}

/** Artifact parser which parses JAR artifacts and returns the contents of those JAR files. */
class JarArtifactParser : ArtifactParser<ArtifactResult.JarArtifact>() {

    override fun parseFile(artifactResult: ArtifactResult.JarArtifact): List<DependencyEntry> {
        val component = artifactResult.component
        return JarFile(artifactResult.file).use { jarFile ->
            jarFile.entries().asSequence().filterNot(JarEntry::isDirectory).map { entry ->
                when {
                    isClassEntry(entry.name) -> DependencyEntry.Class(entry.name, component)
                    else -> DependencyEntry.Default(entry.name, component)
                }
            }.toList()
        }
    }

    private fun isClassEntry(entryName: String): Boolean {
        return entryName.endsWith(".class", ignoreCase = true)
    }
}

sealed interface ArtifactResult {
    data class DefaultArtifact(
        val file: File,
        val resolvedArtifactFile: File,
        val component: String
    ) : ArtifactResult

    data class JarArtifact(val file: File, val component: String) : ArtifactResult
}

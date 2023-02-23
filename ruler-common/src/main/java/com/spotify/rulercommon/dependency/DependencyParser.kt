package com.spotify.rulercommon.dependency

/** Responsible for parsing and extracting entries from dependencies. */
class DependencyParser {

    /** Parses and returns the list of entries contained in all dependencies of the given [project]. */
    fun parse(entries: List<ArtifactResult>): List<DependencyEntry> {
        val result = mutableListOf<DependencyEntry>()

        val jarArtifactParser = JarArtifactParser()
        val defaultArtifactParser = DefaultArtifactParser()
        entries.forEach {
            result += when (it) {
                is ArtifactResult.DefaultArtifact -> {
                    defaultArtifactParser.parseFile(it)
                }
                is ArtifactResult.JarArtifact -> {
                    jarArtifactParser.parseFile(it)
                }
            }
        }
        return result
    }
}

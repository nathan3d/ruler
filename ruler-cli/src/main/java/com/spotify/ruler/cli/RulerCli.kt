package com.spotify.ruler.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import com.spotify.rulercommon.BaseRulerTask
import com.spotify.rulercommon.RulerConfig
import com.spotify.rulercommon.dependency.DependencyComponent
import java.io.File

class RulerCli: CliktCommand(), BaseRulerTask {
    val appBundle by argument().file()
    val dependencyMap by argument().file()

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
        get() = RulerConfig(

        )

    override fun getDependencies(): Map<String, List<DependencyComponent>> {
        TODO("Not yet implemented")
    }

    override fun run() {
        super.run()
    }


}

fun main(args: Array<String>) = RulerCli().main(args)

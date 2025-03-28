description = "Robocode Tank Royale schema for .Net"

plugins {
    base
}

val inputSchemaDir = "${layout.projectDirectory}/../../../schema/schemas"
val generatedOutputDir = "${layout.projectDirectory}/generated"

tasks {
    val dotnetClean by registering(Exec::class) {
        commandLine("dotnet", "clean")
    }

    val dotnetBuild by registering(Exec::class) {
        commandLine("dotnet", "build", "--configuration", "Release")
    }

    val generateSchema by registering(Exec::class) {
        dependsOn(dotnetBuild)

        doFirst {
            mkdir(generatedOutputDir)
        }

        var codeGeneratorPath = "${layout.projectDirectory}/bin/Release/net6.0/CodeGeneratorApp"
        if (System.getProperty("os.name").lowercase().contains("windows")) {
            codeGeneratorPath += ".exe"
        }

        println("codeGeneratorPath: $codeGeneratorPath")
        println(inputSchemaDir)
        println(generatedOutputDir)

        commandLine(
            codeGeneratorPath,
            inputSchemaDir,
            generatedOutputDir,
            "Robocode.TankRoyale.Schema"
        )
    }

    named("clean") {
        dependsOn(dotnetClean)

        doLast {
            delete("bin")
            delete("obj")
            delete("generated")
        }
    }

    named("build") {
        dependsOn(generateSchema)
    }
}

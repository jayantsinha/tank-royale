import build.tasks.FatJar
import proguard.gradle.ProGuardTask

description = "Robocode Tank Royale Observer Window"

val archiveTitle = "Robocode Tank Royale Observer"
group = "dev.robocode.tankroyale"
version = libs.versions.tankroyale.get()

val jarManifestMainClass = "dev.robocode.tankroyale.observer.ObserverAppKt"

base {
    archivesName = "robocode-tankroyale-observer" // renames _all_ archive names
}

val baseArchiveName = "${base.archivesName.get()}-${project.version}"

buildscript {
    dependencies {
        classpath(libs.proguard.gradle)
    }
}

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    alias(libs.plugins.shadow.jar)
    `maven-publish`
    signing
}

dependencies {
    // Project dependencies
    implementation(project(":server"))
    
    // Kotlin and serialization
    implementation(libs.kotlinx.serialization.json)
    
    // WebSocket client for connecting to server
    implementation(libs.java.websocket)
    
    // GUI - Java Swing/AWT (built-in)
    // No additional dependencies needed for basic Swing
    
    // Testing
    testImplementation(testLibs.junit.api)
    testImplementation(testLibs.junit.engine)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar() // required for uploading to Sonatype
    withSourcesJar()
}

tasks {
    test {
        useJUnitPlatform()
    }

    // Run task for development
    val run by registering(JavaExec::class) {
        dependsOn(classes)
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set(jarManifestMainClass)
        args = listOf()
    }

    // Fat JAR task for standalone observer
    val fatJar by registering(FatJar::class) {
        dependsOn(classes)

        inputs.files(configurations.runtimeClasspath)

        // Ensure the fat jar goes to the libs directory
        destinationDirectory.set(layout.buildDirectory.dir("libs"))
        archiveFileName.set("$baseArchiveName-all.jar")

        title.set(archiveTitle)
        mainClass.set(jarManifestMainClass)
    }

    // ProGuard obfuscation task
    val proguard by registering(ProGuardTask::class) {
        dependsOn(fatJar)
        
        val libsDir = layout.buildDirectory.dir("libs").get().asFile
        val inputJar = libsDir.resolve("$baseArchiveName-all.jar")
        val outputJar = libsDir.resolve("$baseArchiveName.jar")

        inputs.file(inputJar)
        outputs.file(outputJar)
        
        configuration("proguard-rules.pro")
        
        injars(inputJar.absolutePath)
        outjars(outputJar.absolutePath)
        
        // Add this to ensure the input jar exists
        doFirst {
            if (!inputJar.exists()) {
                throw GradleException("Input jar file not found: ${inputJar.absolutePath}")
            }
        }
    }

    jar {
        manifest {
            attributes["Main-Class"] = jarManifestMainClass
            attributes["Implementation-Title"] = archiveTitle
            attributes["Implementation-Version"] = archiveVersion
            attributes["Implementation-Vendor"] = "Robocode"
        }
    }
}

// Publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name = archiveTitle
                description = project.description
                url = "https://github.com/robocode-dev/tank-royale"
                
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                
                developers {
                    developer {
                        id = "fnl"
                        name = "Flemming N. Larsen"
                        organization = "Robocode"
                    }
                }
                
                scm {
                    connection = "scm:git:git://github.com/robocode-dev/tank-royale.git"
                    developerConnection = "scm:git:ssh://github.com:robocode-dev/tank-royale.git"
                    url = "https://github.com/robocode-dev/tank-royale/tree/main"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

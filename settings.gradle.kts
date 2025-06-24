rootProject.name = "robocode-tank-royale"

val version: String = providers.gradleProperty("version").get()

// Booter
include("booter")

// Server
include("server")

// GUI app
include("gui-app")

// Observer
include("observer")

// Bot API
include("bot-api:java")
include("bot-api:dotnet")
include("bot-api:dotnet:schema")
//include("bot-api:python")

// Sample Bots archives
include("sample-bots:java")
include("sample-bots:csharp")

// Docs
include("buildDocs")

// Check dependencies with this command:  gradlew dependencyUpdates -Drevision=release
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("tankroyale", version)
        }
        create("testLibs") {
            from(files("gradle/test-libs.versions.toml"))
        }
    }
}
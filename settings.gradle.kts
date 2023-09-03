pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven( "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven( "https://jitpack.io")
    }
}

rootProject.name = "Abrnoc Application"
include(":app")
include(":data")
include(":domain")

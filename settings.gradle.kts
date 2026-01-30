pluginManagement {
    repositories {
        google()                // ✅ Required for Firebase + Android Gradle plugins
        mavenCentral()          // ✅ Standard Kotlin/Compose dependencies
        gradlePluginPortal()    // ✅ Hilt and Kotlin plugins
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WorkHive2"
include(":app")

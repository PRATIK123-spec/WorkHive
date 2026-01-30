// Root build.gradle.kts
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // ✅ Use the latest stable versions (keep only one of each)
        classpath("com.android.tools.build:gradle:8.7.2") // <-- 8.12.3 doesn’t exist; likely a typo
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52")
        classpath("com.google.gms:google-services:4.4.2") // ✅ Keep only once
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

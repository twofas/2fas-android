buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
        classpath("com.google.gms:google-services:4.4.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.6")

    }
}

plugins {
    alias(libs.plugins.agpApplication) apply false
    alias(libs.plugins.agpLibrary) apply false

    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinParcelize) apply false
    alias(libs.plugins.kotlinComposeCompiler) apply false
    alias(libs.plugins.gradleVersions)
    alias(libs.plugins.versionCatalogUpdate)
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("prcheck", Exec::class) {
    commandLine = "./gradlew formatKotlin lintKotlin testDebugUnitTest".split(" ")
}

tasks.register("prformat", Exec::class) {
    commandLine = "./gradlew formatKotlin lintKotlin".split(" ")
}

tasks.register("libs", Exec::class) {
    commandLine = "./gradlew dependencyUpdates".split(" ")
}

tasks.register("libsFormat", Exec::class) {
    commandLine = "./gradlew versionCatalogFormat".split(" ")
}
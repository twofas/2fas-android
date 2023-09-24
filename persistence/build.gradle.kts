@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.twofasapp.persistence"

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(project(":prefs"))
    
    implementation(project(":parsers"))
    implementation(project(":data:notifications"))
    implementation(project(":data:services"))
    implementation(project(":data:browserext"))
    implementation(project(":core:common"))

    implementation(libs.bundles.room)
    implementation(libs.reLinker)
    ksp(libs.roomCompiler)
}
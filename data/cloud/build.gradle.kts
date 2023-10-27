import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.data.cloud"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:storage"))

    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
    implementation(libs.timber)

    api(libs.googleApiClientGson)
    api(libs.googleApiClientAndroid) {
        exclude("org.apache.httpcomponents", "guava-jdk5")
        exclude("com.google.http-client", "google-http-client")
    }
    api(libs.googleAuth) {
        exclude("com.google.http-client", "google-http-client")
        exclude("com.google.http-client", "google-http-client-jackson")
    }
    api(libs.googleDrive) {
        exclude("org.apache.httpcomponents", "guava-jdk5")
        exclude("com.google.http-client", "google-http-client")
    }
}
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.twofasAndroidLibrary)
    alias(libs.plugins.kotlinSerialization)
}

android {
    namespace = "com.twofasapp.data.cloud"
}

dependencies {
    implementation(project(":core:di"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:storage"))

    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinSerialization)
    implementation(libs.timber)

    api("com.google.android.gms:play-services-auth:20.7.0") {
        exclude("com.google.http-client", "google-http-client")
        exclude("com.google.http-client", "google-http-client-jackson")
    }

    api("com.github.rafakob.google-http-java-client:google-http-client-gson:1.43.0")

    api("com.google.api-client:google-api-client-android:2.2.0") {
        exclude("org.apache.httpcomponents", "guava-jdk5")
        exclude("com.google.http-client", "google-http-client")
    }
    api("com.google.apis:google-api-services-drive:v3-rev197-1.25.0") {
        exclude("org.apache.httpcomponents", "guava-jdk5")
        exclude("com.google.http-client", "google-http-client")
    }
}
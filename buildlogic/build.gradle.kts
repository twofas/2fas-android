@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    compileOnly("com.android.tools.build:gradle:8.0.0-alpha11")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
}

gradlePlugin {
    @Suppress("DSL_SCOPE_VIOLATION")
plugins {
        register("TwoFasComposePlugin") {
            id = "twofas.compose"
            implementationClass = "com.twofasapp.buildlogic.TwoFasComposePlugin"
        }

        register("TwoFasAndroidLibraryPlugin") {
            id = "twofas.androidLibrary"
            implementationClass = "com.twofasapp.buildlogic.TwoFasAndroidLibraryPlugin"
        }

        register("TwoFasAndroidApplicationPlugin") {
            id = "twofas.androidApplication"
            implementationClass = "com.twofasapp.buildlogic.TwoFasAndroidApplicationPlugin"
        }
    }
}
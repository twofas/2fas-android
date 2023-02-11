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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
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
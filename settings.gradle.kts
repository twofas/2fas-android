pluginManagement {
    includeBuild("buildlogic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "2fas-android"
include(":truetime")
include(":app")
include(":base")
include(":parsers")
include(":feature:security")
include(":feature:externalimport")
include(":data:session")
include(":core:common")
include(":core:designsystem")
include(":feature:startup")
include(":core:locale")
include(":feature:home")
include(":feature:guides")
include(":data:notifications")
include(":core:network")
include(":data:services")
include(":feature:trash")
include(":feature:about")
include(":data:browserext")
include(":feature:browserext")
include(":core:otp")
include(":feature:appsettings")
include(":feature:qrscan")
include(":core:android")
include(":feature:backup")
include(":core:cipher")
include(":data:cloud")
include(":feature:widget")
include(":feature:permissions")
include(":data:push")
include(":feature:developer")

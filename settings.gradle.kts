pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = ("quies")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("app")
include(":data")

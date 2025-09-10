import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.versions)
    base
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("com.google.devtools.ksp") version libs.versions.ksp apply false
    kotlin("android") apply false
    id("com.google.firebase.crashlytics") version "3.0.1" apply false
    alias(libs.plugins.compose.compiler) apply false
}

allprojects {
    group = PUBLISHING_GROUP
}

val detektFormatting = libs.detekt.formatting

subprojects {
    apply {
        plugin("io.gitlab.arturbosch.detekt")
    }

    detekt {
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    }

    dependencies {
        detektPlugins(detektFormatting)
    }
}

tasks {
    withType<DependencyUpdatesTask>().configureEach {
        rejectVersionIf {
            candidate.version.isStableVersion().not()
        }
    }
}

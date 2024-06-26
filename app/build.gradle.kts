import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.play.publisher)
    alias(libs.plugins.simple.flank)
    id("com.android.application")
    id("kotlin-kapt")
    kotlin("android")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = libs.versions.compile.sdk.version.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.version.get().toInt()
        targetSdk = libs.versions.compile.sdk.version.get().toInt()
        namespace = "dev.simonas.quies"

        applicationId = AppCoordinates.APP_ID
        versionCode = AppCoordinates.APP_VERSION_CODE
        versionName = AppCoordinates.APP_VERSION_NAME
        testInstrumentationRunner = "dev.simonas.quies.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
    signingConfigs {
        getByName("debug") {
            keyAlias = "androiddebugkey"
            keyPassword = "android"
            storeFile = rootProject.file("app/debug.keystore")
            storePassword = "android"
        }
        create("release") {
            // Will be created by CI using $RELEASE_KEYSTORE_PROPS
            val keystoreProps = rootProject.file("app/keystore.properties")
            if (keystoreProps.exists()) {
                val properties = Properties().apply {
                    load(keystoreProps.reader())
                }
                keyAlias = properties.getProperty("keystoreAlias")
                keyPassword = properties.getProperty("aliasPassword")
                // Will be created by CI using $RELEASE_KEYSTORE
                storeFile = rootProject.file("app/release.keystore")
                storePassword = properties.getProperty("keystorePassword")
            } else {
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                storeFile = rootProject.file("app/debug.keystore")
                storePassword = "android"
            }
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }
    lint {
        disable.add("GradleDependency")
        disable.add("AndroidGradlePluginVersion")
        warningsAsErrors = true
        abortOnError = true
        baseline = rootProject.file("app/lint-baseline.xml")
    }
    packaging {
        resources.excludes += "DebugProbesKt.bin"
    }
    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraint.layout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.colormath)
    implementation(libs.colormath.ext.jetpack.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.lifecycle)
    implementation(libs.compose.material3)
    implementation(libs.compose.navigation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.javapoet)
    implementation(libs.kotlin.math)
    implementation(platform(libs.firebase.bom))
    implementation(project(":data"))
    implementation(libs.datastore)
    implementation(libs.datastore.preferences)
    implementation(libs.mixpanel)

    kapt(libs.hilt.android.compiler)

    debugImplementation(libs.compose.ui.test.manifest)

    kaptTest(libs.hilt.android.compiler)
    testImplementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.hilt.android)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)

    androidTestImplementation(libs.hilt.android)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.hilt.testing)
    androidTestImplementation(libs.mockito.android)
    kaptAndroidTest(libs.hilt.android.compiler)
}

simpleFlank {
    // Will be created at GitHub CI job runtime via $FLANK_SERVICE_ACCOUNT_KEY secret.
    credentialsFile.set(file("flank-service-account-key.json"))
    devices.set(
        listOf(
            io.github.flank.gradle.Device(
                id = "oriole",
                osVersion = 31,
                make =  "Google",
                model = "Pixel 6",
            )
        )
    )
    recordVideo = true
}

play {
    defaultToAppBundles.set(true)
    // Will be created at GitHub CI job runtime via $PUBLISHER_SERVICE_ACCOUNT_KEY secret.
    serviceAccountCredentials.set(file("publisher-service-account-key.json"))
    track.set("internal")
}

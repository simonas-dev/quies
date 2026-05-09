plugins {
    `kotlin-dsl`
}
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.kgp)
    implementation(libs.agp)
    // otherwise app:hiltAggregateDepsDebug fails because it's failing to find
    // 'java.lang.String com.squareup.javapoet.ClassName.canonicalName()'
    implementation(libs.javapoet)
}

kotlin {
    jvmToolchain(21)
}
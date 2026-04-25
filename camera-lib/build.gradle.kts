plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.koin.compiler)
}

android {
    namespace = "com.lonwulf.labs.camera_lib"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies{
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    api(libs.androidx.ui.graphics)
    api(libs.androidx.ui.tooling.preview)
    api(libs.androidx.material3)
    api(libs.material.icons)
    api(libs.navigation.compose)
    implementation(libs.koin.android)
//    implementation(libs.koin.viewmodel)

    api(libs.ml.kit.barcode.scanner)
    implementation(libs.ml.kit.obj.detection)
    implementation(libs.ml.kit.obj.detection.custom)
    implementation(libs.camera2)
    implementation(libs.camera2.lifecycle)
    implementation(libs.cameraView)
    implementation(libs.permissions)
    implementation("com.google.guava:guava:27.1-android")

}

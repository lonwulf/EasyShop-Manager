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
//todo: fix this when doing release
//    buildTypes {
//        getByName("release") {
//            multiDexKeepProguard = file("multidex-config.pro")
//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
//            )
//        }
//        getByName("debug") {
//            isMinifyEnabled = false
//        }
//    }
}

dependencies{
    implementation(project(":presentation"))
    implementation(project(":core"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    api(libs.navigation.compose)
    implementation(libs.koin.android)

    api(libs.ml.kit.barcode.scanner)
    implementation(libs.ml.kit.obj.detection)
    implementation(libs.ml.kit.obj.detection.custom)
    implementation(libs.camera2)
    implementation(libs.camera2.lifecycle)
    implementation(libs.cameraView)
    implementation(libs.permissions)
    implementation("com.google.guava:guava:27.1-android")

}
